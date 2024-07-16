package com.ucpb.tfs.domain.casa.services;

import com.ucpb.tfs.domain.casa.RefCasaAccount;
import com.ucpb.tfs.domain.casa.exceptions.RefCasaAccountParseException;
import com.ucpb.tfs.domain.casa.infrasctructure.repositories.RefCasaAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marv on 2/27/14.
 */

@Component
public class RefCasaAccountServiceImpl implements RefCasaAccountService {

//    private static final String VALID_INPUT = "([\\w]{7})([\\w\\W\\s]{20})([\\d]{7})([\\d]{12})(D|S)([A-Z]{3})([\\s]{150})";
    private static final String VALID_INPUT = "([\\w]{7})([\\w\\W\\s]{20})([\\d]{7})([\\d]{12})(D|S)([A-Z]{3})";

    private static final String CASA_FILENAME_PREFIX = "DDU052";

    @Autowired
    RefCasaAccountRepository refCasaAccountRepository;

    @Autowired
    PropertiesFactoryBean appProperties;

    @Override
    public List<RefCasaAccount> findByCifNumberAndCurrency(String cifNumber, String currency) {
        return refCasaAccountRepository.findRefCasaAccountMatching(cifNumber, currency);
    }

    @Override
    public void save(RefCasaAccount refCasaAccount) {
        refCasaAccountRepository.persist(refCasaAccount);
    }

    private void clearRefCasaAccounts() {
        refCasaAccountRepository.deleteAllRefCasaAccount();
    }

    private String getFileName(String casaFilename, String extension) {
        String fileName = casaFilename + "." + extension;
        System.out.println("file name generated : " + fileName);

        return fileName;
    }

    private String getBackupFileName(String casaFilenamePrefix, String extension) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHssmm");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        String fileName = casaFilenamePrefix + simpleDateFormat.format(calendar.getTime()) + "." + extension;
        System.out.println("backup file name generated : " + fileName);

        return fileName;
    }

    private void backupRefCasaAccountsFile(File file, File backupFile) throws IOException {
        InputStream inStream = new FileInputStream(file);
        OutputStream outStream = new FileOutputStream(backupFile);

        byte[] buffer = new byte[1024];

        int length;
        //copy the file content in bytes
        while ((length = inStream.read(buffer)) > 0) {
            outStream.write(buffer, 0, length);
        }

        inStream.close();
        outStream.close();

        System.out.println("File is copied successful!");
    }

    private void parseFile(File file) throws RefCasaAccountParseException, IOException {
        InputStream inputStream = new FileInputStream(file);

        String casaAccountString = "";

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        if (inputStream != null) {
            while ((casaAccountString = reader.readLine()) != null) {
                Map<String, String> casaAccountMap = parseCasaAccountString(casaAccountString);

                RefCasaAccount refCasaAccount = new RefCasaAccount(casaAccountMap.get("cifNumber"),
                        casaAccountMap.get("currency"),
                        casaAccountMap.get("accountNumber"),
                        casaAccountMap.get("accountName"),
                        casaAccountMap.get("accountType"));

                refCasaAccountRepository.persist(refCasaAccount);
            }

            file.delete();
        }
    }

    public void populateRefCasaAccount() throws Exception {
        // delete all records first
        clearRefCasaAccounts();

        String inputDirectory = appProperties.getObject().getProperty("casa.accounts.input.directory");
        String backupDirectory = appProperties.getObject().getProperty("casa.accounts.backup.directory");

        try {
            if (inputDirectory == null || backupDirectory == null) {
                throw new Exception("CASA directory not set in properties.");
            }

            File file = new File(inputDirectory + "/" + getFileName(CASA_FILENAME_PREFIX, "TXT"));
            File backupFile = new File(backupDirectory + "/" + getBackupFileName(CASA_FILENAME_PREFIX + "_", "TXT"));

            // backup file first
            backupRefCasaAccountsFile(file, backupFile);

            parseFile(file);

        } catch (FileNotFoundException fnfe) {
        	System.err.println("----------FileNotFoundException----------");
            fnfe.printStackTrace();

            File file = new File(inputDirectory + "/" + getFileName(CASA_FILENAME_PREFIX, "txt"));
            File backupFile = new File(backupDirectory + "/" + getBackupFileName(CASA_FILENAME_PREFIX + "_", "txt"));

            // backup file first
            backupRefCasaAccountsFile(file, backupFile);

            parseFile(file);

        } catch (IOException ioe) {
        	System.err.println("----------IOException----------");
            ioe.printStackTrace();

            throw new Exception("File not found.");
        } catch (RefCasaAccountParseException rcape) {
        	System.err.println("----------RefCasaAccountParseException----------");
            rcape.printStackTrace();

            throw new Exception("Content format is invalid.");
        }
    }

    private Map<String, String> parseCasaAccountString(String casaAccountString) throws RefCasaAccountParseException {
        if (casaAccountString.matches(VALID_INPUT)) {
            Matcher matcher = Pattern.compile(VALID_INPUT).matcher(casaAccountString);
            if(matcher.find()){
                return mapToCasaResponse(toStringArray(matcher));
            }
        } else {
            throw new RefCasaAccountParseException("Invalid CASA Account String format.");
        }

        return null;
    }

    private String[] toStringArray(Matcher matcher){
        String[] map = new String[matcher.groupCount()];
        for(int ctr = 0; ctr < matcher.groupCount(); ctr++){
            map[ctr] = matcher.group(ctr+1);
        }
        return map;
    }

    private Map<String, String> mapToCasaResponse(String[] casaAccountString) {
        Map<String, String> casaAccountMap = new HashMap<String, String>();
        casaAccountMap.put("cifNumber", casaAccountString[0]);
        casaAccountMap.put("accountName", casaAccountString[1]);
        casaAccountMap.put("accountNumber", casaAccountString[3]);
        casaAccountMap.put("accountType", casaAccountString[4]);
        casaAccountMap.put("currency", casaAccountString[5]);

        return casaAccountMap;
    }

    // TEST
    @Override
    public void testRegex() {
        String testString = "A000569ASSET MARKETING     0000000000010104549DPHP                                                                                                                                                      ";

        if (testString.matches(VALID_INPUT)) {
            System.out.println("input matched");
            Matcher matcher = Pattern.compile(VALID_INPUT).matcher(testString);
            if(matcher.find()){
                Map<String, String> casaResponse = mapToCasaResponse(toStringArray(matcher));
            }
        } else {
            System.out.println("input not matched");
        }
    }
}
