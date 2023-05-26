package com.jim.demo.swaphot;

import com.sun.tools.attach.VirtualMachine;

public class MonitoredVmInfo {

	public final int processId;
	public final VirtualMachine monitoredVm;

	public MonitoredVmInfo(int processId, VirtualMachine monitoredVm) {
		this.processId = processId;
		this.monitoredVm = monitoredVm;
	}
}
