package com.jim.demo.swaphot;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class JVMUtils {

	public static int getProcessId(Class<?> cls) throws IOException, AttachNotSupportedException {
		MonitoredVmInfo monitoredVm = getMonitoredVm(cls);
		return monitoredVm == null ? -1 : monitoredVm.processId;
	}

	public static MonitoredVmInfo getMonitoredVm(Class<?> cls) throws IOException, AttachNotSupportedException {
		List<VirtualMachineDescriptor> virtualMachineDescriptors = VirtualMachine.list();
		for (VirtualMachineDescriptor virtualMachineDescriptor : virtualMachineDescriptors) {
			if (virtualMachineDescriptor.displayName().startsWith(cls.getName())) {
				VirtualMachine virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);
				return new MonitoredVmInfo(Integer.parseInt(virtualMachineDescriptor.id()), virtualMachine);
			}
		}
		return null;
	}

	public static boolean consumeVirtualMachine(Class<?> cls, Consumer<VirtualMachine> consumer) throws IOException, AttachNotSupportedException {
		List<VirtualMachineDescriptor> virtualMachineDescriptors = VirtualMachine.list();
		for (VirtualMachineDescriptor virtualMachineDescriptor : virtualMachineDescriptors) {
			if (virtualMachineDescriptor.displayName().startsWith(cls.getName())) {
				VirtualMachine virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);
				consumer.accept(virtualMachine);
				virtualMachine.detach();
				return true;
			}
		}
		return false;
	}
}
