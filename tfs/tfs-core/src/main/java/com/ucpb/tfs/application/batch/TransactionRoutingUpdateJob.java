package com.ucpb.tfs.application.batch;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.ucpb.tfs.batch.dao.BatchProcessDao;
import com.ucpb.tfs.batch.job.SpringJob;
import com.ucpb.tfs.batch.util.FileUtil;

public class TransactionRoutingUpdateJob implements SpringJob {

	BatchProcessDao batchProcessDao;
	private File file = new File("/opt/tfs/BATCH_UPLOADS/USER_ROUTING.txt");
	
	public void execute() throws IOException {
				
		List<String> document = FileUtil.read(file);
		System.out.println("deleting routing entries...");
		batchProcessDao.deleteRouting();
		System.out.println("...routing entries deleted");
		for(String documentEntry : document){
			if(documentEntry.contains("|")){
				String[] routingEntry = documentEntry.split("\\|");
				if(!routingEntry[0].contains(" ") && routingEntry.length == 4){
					if(batchProcessDao.checkIfExistingUser(routingEntry[0]) > 0 && batchProcessDao.checkIfValidRole(routingEntry[3]) > 0){
						System.out.println("inserting: " + routingEntry[0] + ", " + routingEntry[1] + ", " + routingEntry[2] + ", " + routingEntry[3]);
						batchProcessDao.insertRouting(routingEntry[0], routingEntry[1], routingEntry[2], routingEntry[3]);
					} else {
						System.err.println("error in: " + routingEntry[0] + ", " + routingEntry[1] + ", " + routingEntry[2] + ", " + routingEntry[3]);
					}
				} else {
					System.err.println("malformat: " + documentEntry);
				}
			} else {
				System.err.println("skipped: " + documentEntry);
			}
		}
	}

	public void execute(String reportDate) {

	}
	
	public void setBatchProcessDao(BatchProcessDao batchProcessDao){
		this.batchProcessDao = batchProcessDao;
	}
}
