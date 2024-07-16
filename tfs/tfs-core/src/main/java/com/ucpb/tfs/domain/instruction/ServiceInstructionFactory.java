/**
 * 
 */
package com.ucpb.tfs.domain.instruction;

import com.incuventure.ddd.domain.annotations.DomainFactory;

/**
 * @author Val
 *
 */
@DomainFactory
public interface ServiceInstructionFactory {

	public ServiceInstruction createInstance();
}
