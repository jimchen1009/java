package com.jim.demo.swaphot;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class JVMUtils {

	public static int getProcessId(Class<?> cls) throws MonitorException, URISyntaxException {
		MonitoredVmInfo monitoredVm = getMonitoredVm(cls);
		return monitoredVm == null ? -1 : monitoredVm.processId;
	}

	public static MonitoredVmInfo getMonitoredVm(Class<?> cls) throws MonitorException, URISyntaxException {
		// 获取监控主机
		MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
		// 取得所有在活动的虚拟机集合
		Set<Integer> activeVms = new HashSet<>(local.activeVms());
		// 遍历集合，输出PID和进程名
		for (Integer activeVm : activeVms) {
			MonitoredVm monitoredVm = local.getMonitoredVm(new VmIdentifier("//" + activeVm));
			String mainClass = MonitoredVmUtil.mainClass(monitoredVm, true);
			if (cls.getName().equals(mainClass)){
				return new MonitoredVmInfo(activeVm, monitoredVm);
			}
		}
		return null;
	}

	public static boolean consumeVirtualMachine(Class<?> cls, Consumer<VirtualMachine> consumer) throws IOException, AttachNotSupportedException {
		List<VirtualMachineDescriptor> virtualMachineDescriptors = VirtualMachine.list();
		for (VirtualMachineDescriptor virtualMachineDescriptor : virtualMachineDescriptors) {
			if (virtualMachineDescriptor.displayName().indexOf(cls.getName()) == 0) {
				VirtualMachine virtualMachine = VirtualMachine.attach(virtualMachineDescriptor.id());
				consumer.accept(virtualMachine);
				virtualMachine.detach();
				return true;
			}
		}
		return false;
	}
}
