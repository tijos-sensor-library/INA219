package tijos.framework.sensor.ina219;

import java.io.IOException;

import tijos.framework.devicecenter.TiI2CMaster;
import tijos.framework.sensor.ina219.TiINA219;
import tijos.util.Delay;

public class TiINA219Sample {

	public static void main(String[] args) {
		try {

			/*
			 * 定义使用的TiI2CMaster port
			 */
			int i2cPort0 = 0;

			/*
			 * 资源分配， 将i2cPort0分配给TiI2CMaster对象i2c0
			 */
			TiI2CMaster i2c0 = TiI2CMaster.open(i2cPort0);

			TiINA219 ina219 = new TiINA219(i2c0);

			// Initialize the INA219.By default the initialization will use the
			// largest range (32V, 2A).
			// However you can call a setCalibration function to change this
			// range (see comments).
			ina219.setCalibration_32V_2A();

			System.out.println("Measuring voltage and current with INA219 ...");

			int num = 100;
			while (num-- > 0) {
				try {

					double shuntvoltage = 0;
					double busvoltage = 0;
					double current_mA = 0;
					double loadvoltage = 0;

					shuntvoltage = ina219.getShuntVoltage_mV();
					busvoltage = ina219.getBusVoltage_V();
					current_mA = ina219.getCurrent_mA();
					loadvoltage = busvoltage + (shuntvoltage / 1000);

					System.out.print("Bus Voltage:   ");
					System.out.print(busvoltage);
					System.out.println(" V");
					System.out.print("Shunt Voltage: ");
					System.out.print(shuntvoltage);
					System.out.println(" mV");
					System.out.print("Load Voltage:  ");
					System.out.print(loadvoltage);
					System.out.println(" V");
					System.out.print("Current:       ");
					System.out.print(current_mA);
					System.out.println(" mA");
					System.out.println("");

					Delay.msDelay(2000);
				} catch (Exception ex) {

					ex.printStackTrace();
				}

			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}

}
