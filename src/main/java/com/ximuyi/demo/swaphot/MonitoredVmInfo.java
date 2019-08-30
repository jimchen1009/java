package com.ximuyi.demo.swaphot;

import sun.jvmstat.monitor.MonitoredVm;

public class MonitoredVmInfo {

	public final int processId;
	public final MonitoredVm monitoredVm;

	public MonitoredVmInfo(int processId, MonitoredVm monitoredVm) {
		this.processId = processId;
		this.monitoredVm = monitoredVm;
	}
}
