/**
 * 
 */
package com.ucpb.tfs.domain.instruction.infrastructure.factories;

import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionFactory;
import org.springframework.stereotype.Component;

/**
 * @author Val
 *
 */
@Component
public class ServiceInstructionFactoryImpl implements ServiceInstructionFactory {

	/* (non-Javadoc)
	 * @see com.ucpb.tfs.domain.instruction.ServiceInstructionFactory#createInstance()
	 */
	@Override
	public ServiceInstruction createInstance() {
		// return new ServiceInstruction();
        return null;
	}
}
