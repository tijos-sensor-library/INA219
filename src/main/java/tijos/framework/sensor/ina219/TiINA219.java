package tijos.framework.sensor.ina219;

import java.io.IOException;
import tijos.framework.devicecenter.TiI2CMaster;
import tijos.util.BigBitConverter;

/*Supply measurement chip (TI INA 219)
http://www.ti.com/product/ina219

The INA219 is a high-side current shunt and power monitor 
with an I2C interface. The INA219 monitors both shunt drop
and supply voltage, with programmable conversion times and 
filtering. A programmable calibration value, combined with 
an internal multiplier, enables direct readouts in amperes. 
An additional multiplying register calculates power in watt. 
The I2C interface features 16 programmable address
*/

/**
 * TiJOS INA219 High-Side Measurement,Bi-Directional CURRENT/POWER MONITOR sensor library 
 * Based on original C library from Ardino by adafruit  
 * https://github.com/adafruit/Adafruit_INA219
 * 
 * @author TiJOS 
 */

class TiINA219Register {

	// CONFIG REGISTER (R/W)
	public static final int INA219_REG_CONFIG = 0x00;

	// SHUNT VOLTAGE REGISTER (R)
	public static final int INA219_REG_SHUNTVOLTAGE = 0x01;

	// BUS VOLTAGE REGISTER =R;
	public static final int INA219_REG_BUSVOLTAGE = 0x02;
	// POWER REGISTER =R;
	public static final int INA219_REG_POWER = 0x03;
	// CURRENT REGISTER =R;
	public static final int INA219_REG_CURRENT = 0x04;
	// CALIBRATION REGISTER =R/W;
	public static final int INA219_REG_CALIBRATION = 0x05;
}

/**
 * 
 * @author Administrator
 *
 */
public class TiINA219 {

	/////////// ADDRESS /////////////////////////////
	// 7bit address = 0b1000000(0x40)
	// G=GND, V=VS+, A=SDA, L=SCL
	// e.g. _VG: A1=VS+, A0=GND
	// -> Please make sure your H/W configuration
	// Set data into "addr"
	public static final int INA219_ADDR_GG = (0x40);
	public static final int INA219_ADDR_GV = (0x41);
	public static final int INA219_ADDR_GA = (0x42);
	public static final int INA219_ADDR_GL = (0x43);
	public static final int INA219_ADDR_VG = (0x44);
	public static final int INA219_ADDR_VV = (0x45);
	public static final int INA219_ADDR_VA = (0x46);
	public static final int INA219_ADDR_VL = (0x47);
	public static final int INA219_ADDR_AG = (0x48);
	public static final int INA219_ADDR_AV = (0x49);
	public static final int INA219_ADDR_AA = (0x4a);
	public static final int INA219_ADDR_AL = (0x4b);
	public static final int INA219_ADDR_LG = (0x4c);
	public static final int INA219_ADDR_LV = (0x4d);
	public static final int INA219_ADDR_LA = (0x4e);
	public static final int INA219_ADDR_LL = (0x4f);

	/////////// PARAMETER SETTING ///////////////////

	// Bus Voltage Range Mask
	public static final int INA219_CONFIG_BVOLTAGERANGE_MASK = 0x2000;
	// 0-16V Range
	public static final int INA219_CONFIG_BVOLTAGERANGE_16V = 0x0000;
	// 0-32V Range
	public static final int INA219_CONFIG_BVOLTAGERANGE_32V = 0x2000;
	// Gain Mask
	public static final int INA219_CONFIG_GAIN_MASK = 0x1800;
	// Gain 1, 40mV Range
	public static final int INA219_CONFIG_GAIN_1_40MV = 0x0000;
	// Gain 2, 80mV Range
	public static final int INA219_CONFIG_GAIN_2_80MV = 0x0800;
	// Gain 4, 160mV Range
	public static final int INA219_CONFIG_GAIN_4_160MV = 0x1000;
	// Gain 8, 320mV Range
	public static final int INA219_CONFIG_GAIN_8_320MV = 0x1800;

	// Bus ADC Resolution Mask
	public static final int INA219_CONFIG_BADCRES_MASK = 0x0780;
	// 9-bit bus res = 0..511
	public static final int INA219_CONFIG_BADCRES_9BIT = 0x0080;
	// 10-bit bus res = 0..1023
	public static final int INA219_CONFIG_BADCRES_10BIT = 0x0100;
	// 11-bit bus res = 0..2047
	public static final int INA219_CONFIG_BADCRES_11BIT = 0x0200;
	// 12-bit bus res = 0..4097
	public static final int INA219_CONFIG_BADCRES_12BIT = 0x0400;

	// Shunt ADC Resolution and Averaging Mask
	public static final int INA219_CONFIG_SADCRES_MASK = 0x0078;
	// 1 x 9-bit shunt sample
	public static final int INA219_CONFIG_SADCRES_9BIT_1S_84US = 0x0000;
	// 1 x 10-bit shunt sample
	public static final int INA219_CONFIG_SADCRES_10BIT_1S_148US = 0x0008;
	// 1 x 11-bit shunt sample
	public static final int INA219_CONFIG_SADCRES_11BIT_1S_276US = 0x0010;
	// 1 x 12-bit shunt sample
	public static final int INA219_CONFIG_SADCRES_12BIT_1S_532US = 0x0018;
	// 2 x 12-bit shunt samples averaged together
	public static final int INA219_CONFIG_SADCRES_12BIT_2S_1060US = 0x0048;
	// 4 x 12-bit shunt samples averaged together
	public static final int INA219_CONFIG_SADCRES_12BIT_4S_2130US = 0x0050;
	// 8 x 12-bit shunt samples averaged together
	public static final int INA219_CONFIG_SADCRES_12BIT_8S_4260US = 0x0058;
	// 16 x 12-bit shunt samples averaged together
	public static final int INA219_CONFIG_SADCRES_12BIT_16S_8510US = 0x0060;
	// 32 x 12-bit shunt samples averaged together
	public static final int INA219_CONFIG_SADCRES_12BIT_32S_17MS = 0x0068;
	// 64 x 12-bit shunt samples averaged together
	public static final int INA219_CONFIG_SADCRES_12BIT_64S_34MS = 0x0070;
	// 128 x 12-bit shunt samples averaged together
	public static final int INA219_CONFIG_SADCRES_12BIT_128S_69MS = 0x0078;

	// Operating Mode Mask
	public static final int INA219_CONFIG_MODE_MASK = 0x0007;
	public static final int INA219_CONFIG_MODE_POWERDOWN = 0x0000;
	public static final int INA219_CONFIG_MODE_SVOLT_TRIGGERED = 0x0001;
	public static final int INA219_CONFIG_MODE_BVOLT_TRIGGERED = 0x0002;
	public static final int INA219_CONFIG_MODE_SANDBVOLT_TRIGGERED = 0x0003;
	public static final int INA219_CONFIG_MODE_ADCOFF = 0x0004;
	public static final int INA219_CONFIG_MODE_SVOLT_CONTINUOUS = 0x0005;
	public static final int INA219_CONFIG_MODE_BVOLT_CONTINUOUS = 0x0006;
	public static final int INA219_CONFIG_MODE_SANDBVOLT_CONTINUOUS = 0x0007;

	/**
	 * TiI2CMaster object
	 */
	private TiI2CMaster i2cmObj;

	// I2C Address
	public int i2cSlaveAddr = INA219_ADDR_GG;

	byte[] data = new byte[8];

	int ina219_i2caddr;
	long ina219_calValue;
	// The following multipliers are used to convert raw current and power
	// values to mA and mW, taking into account the current config settings
	long ina219_currentDivider_mA = 0;
	long ina219_powerDivider_mW = 0;

	/**
	 * Initialize object with i2c communication object, default slave address is
	 * 0x40 (GND GND)
	 * 
	 * @param i2c
	 *            I2C master object for communication
	 */
	public TiINA219(TiI2CMaster i2c) {
		this(i2c, INA219_ADDR_GG);
	}

	/**
	 * Initialize object with i2c communication object and slave address
	 * 
	 * @param i2c
	 *            I2C master object for communication
	 * @param addr
	 *            slave address as the device HW configuration
	 */
	public TiINA219(TiI2CMaster i2c, int addr) {
		this.i2cmObj = i2c;
		this.i2cSlaveAddr = addr;
	}

	/**
	 * Configures to INA219 to be able to measure up to 32V and 2A of current.
	 * Each unit of current corresponds to 100uA, and each unit of power
	 * corresponds to 2mW. Counter overflow occurs at 3.2A. These calculations
	 * assume a 0.1 ohm resistor is present
	 * 
	 * @throws IOException
	 */
	public void setCalibration_32V_2A() throws IOException {
		// By default we use a pretty huge range for the input voltage,
		// which probably isn't the most appropriate choice for system
		// that don't use a lot of power. But all of the calculations
		// are shown below if you want to change the settings. You will
		// also need to change any relevant register settings, such as
		// setting the VBUS_MAX to 16V instead of 32V, etc.

		// VBUS_MAX = 32V (Assumes 32V, can also be set to 16V)
		// VSHUNT_MAX = 0.32 (Assumes Gain 8, 320mV, can also be 0.16, 0.08,
		// 0.04)
		// RSHUNT = 0.1 (Resistor value in ohms)

		// 1. Determine max possible current
		// MaxPossible_I = VSHUNT_MAX / RSHUNT
		// MaxPossible_I = 3.2A

		// 2. Determine max expected current
		// MaxExpected_I = 2.0A

		// 3. Calculate possible range of LSBs (Min = 15-bit, Max = 12-bit)
		// MinimumLSB = MaxExpected_I/32767
		// MinimumLSB = 0.000061 (61uA per bit)
		// MaximumLSB = MaxExpected_I/4096
		// MaximumLSB = 0,000488 (488uA per bit)

		// 4. Choose an LSB between the min and max values
		// (Preferrably a roundish number close to MinLSB)
		// CurrentLSB = 0.0001 (100uA per bit)

		// 5. Compute the calibration register
		// Cal = trunc (0.04096 / (Current_LSB * RSHUNT))
		// Cal = 4096 (0x1000)

		ina219_calValue = 4096;

		// 6. Calculate the power LSB
		// PowerLSB = 20 * CurrentLSB
		// PowerLSB = 0.002 (2mW per bit)

		// 7. Compute the maximum current and shunt voltage values before
		// overflow
		//
		// Max_Current = Current_LSB * 32767
		// Max_Current = 3.2767A before overflow
		//
		// If Max_Current > Max_Possible_I then
		// Max_Current_Before_Overflow = MaxPossible_I
		// Else
		// Max_Current_Before_Overflow = Max_Current
		// End If
		//
		// Max_ShuntVoltage = Max_Current_Before_Overflow * RSHUNT
		// Max_ShuntVoltage = 0.32V
		//
		// If Max_ShuntVoltage >= VSHUNT_MAX
		// Max_ShuntVoltage_Before_Overflow = VSHUNT_MAX
		// Else
		// Max_ShuntVoltage_Before_Overflow = Max_ShuntVoltage
		// End If

		// 8. Compute the Maximum Power
		// MaximumPower = Max_Current_Before_Overflow * VBUS_MAX
		// MaximumPower = 3.2 * 32V
		// MaximumPower = 102.4W

		// Set multipliers to convert raw current/power values
		ina219_currentDivider_mA = 10; // Current LSB = 100uA per bit (1000/100
										// = 10)
		ina219_powerDivider_mW = 2; // Power LSB = 1mW per bit (2/1)

		// Set Calibration register to 'Cal' calculated above
		wireWriteRegister(TiINA219Register.INA219_REG_CALIBRATION, (int) ina219_calValue);

		// Set Config register to take into account the settings above
		int config = INA219_CONFIG_BVOLTAGERANGE_32V | INA219_CONFIG_GAIN_8_320MV | INA219_CONFIG_BADCRES_12BIT
				| INA219_CONFIG_SADCRES_12BIT_1S_532US | INA219_CONFIG_MODE_SANDBVOLT_CONTINUOUS;

		wireWriteRegister(TiINA219Register.INA219_REG_CONFIG, config);
	}

	/**
	 * Configures to INA219 to be able to measure up to 32V and 1A of current.
	 * Each unit of current corresponds to 40uA, and each unit of power
	 * corresponds to 800W. Counter overflow occurs at 1.3A.
	 */
	public void setCalibration_32V_1A() throws IOException {
		// By default we use a pretty huge range for the input voltage,
		// which probably isn't the most appropriate choice for system
		// that don't use a lot of power. But all of the calculations
		// are shown below if you want to change the settings. You will
		// also need to change any relevant register settings, such as
		// setting the VBUS_MAX to 16V instead of 32V, etc.

		// VBUS_MAX = 32V (Assumes 32V, can also be set to 16V)
		// VSHUNT_MAX = 0.32 (Assumes Gain 8, 320mV, can also be 0.16, 0.08,
		// 0.04)
		// RSHUNT = 0.1 (Resistor value in ohms)

		// 1. Determine max possible current
		// MaxPossible_I = VSHUNT_MAX / RSHUNT
		// MaxPossible_I = 3.2A

		// 2. Determine max expected current
		// MaxExpected_I = 1.0A

		// 3. Calculate possible range of LSBs (Min = 15-bit, Max = 12-bit)
		// MinimumLSB = MaxExpected_I/32767
		// MinimumLSB = 0.0000305 (30.5�A per bit)
		// MaximumLSB = MaxExpected_I/4096
		// MaximumLSB = 0.000244 (244�A per bit)

		// 4. Choose an LSB between the min and max values
		// (Preferrably a roundish number close to MinLSB)
		// CurrentLSB = 0.0000400 (40�A per bit)

		// 5. Compute the calibration register
		// Cal = trunc (0.04096 / (Current_LSB * RSHUNT))
		// Cal = 10240 (0x2800)

		ina219_calValue = 10240;

		// 6. Calculate the power LSB
		// PowerLSB = 20 * CurrentLSB
		// PowerLSB = 0.0008 (800�W per bit)

		// 7. Compute the maximum current and shunt voltage values before
		// overflow
		//
		// Max_Current = Current_LSB * 32767
		// Max_Current = 1.31068A before overflow
		//
		// If Max_Current > Max_Possible_I then
		// Max_Current_Before_Overflow = MaxPossible_I
		// Else
		// Max_Current_Before_Overflow = Max_Current
		// End If
		//
		// ... In this case, we're good though since Max_Current is less than
		// MaxPossible_I
		//
		// Max_ShuntVoltage = Max_Current_Before_Overflow * RSHUNT
		// Max_ShuntVoltage = 0.131068V
		//
		// If Max_ShuntVoltage >= VSHUNT_MAX
		// Max_ShuntVoltage_Before_Overflow = VSHUNT_MAX
		// Else
		// Max_ShuntVoltage_Before_Overflow = Max_ShuntVoltage
		// End If

		// 8. Compute the Maximum Power
		// MaximumPower = Max_Current_Before_Overflow * VBUS_MAX
		// MaximumPower = 1.31068 * 32V
		// MaximumPower = 41.94176W

		// Set multipliers to convert raw current/power values
		ina219_currentDivider_mA = 25; // Current LSB = 40uA per bit (1000/40 =
										// 25)
		ina219_powerDivider_mW = 1; // Power LSB = 800�W per bit

		// Set Calibration register to 'Cal' calculated above
		wireWriteRegister(TiINA219Register.INA219_REG_CALIBRATION, (int) ina219_calValue);

		// Set Config register to take into account the settings above
		int config = INA219_CONFIG_BVOLTAGERANGE_32V | INA219_CONFIG_GAIN_8_320MV | INA219_CONFIG_BADCRES_12BIT
				| INA219_CONFIG_SADCRES_12BIT_1S_532US | INA219_CONFIG_MODE_SANDBVOLT_CONTINUOUS;
		wireWriteRegister(TiINA219Register.INA219_REG_CONFIG, config);
	}

	public void setCalibration_16V_400mA() throws IOException {

		// Calibration which uses the highest precision for
		// current measurement (0.1mA), at the expense of
		// only supporting 16V at 400mA max.

		// VBUS_MAX = 16V
		// VSHUNT_MAX = 0.04 (Assumes Gain 1, 40mV)
		// RSHUNT = 0.1 (Resistor value in ohms)

		// 1. Determine max possible current
		// MaxPossible_I = VSHUNT_MAX / RSHUNT
		// MaxPossible_I = 0.4A

		// 2. Determine max expected current
		// MaxExpected_I = 0.4A

		// 3. Calculate possible range of LSBs (Min = 15-bit, Max = 12-bit)
		// MinimumLSB = MaxExpected_I/32767
		// MinimumLSB = 0.0000122 (12uA per bit)
		// MaximumLSB = MaxExpected_I/4096
		// MaximumLSB = 0.0000977 (98uA per bit)

		// 4. Choose an LSB between the min and max values
		// (Preferrably a roundish number close to MinLSB)
		// CurrentLSB = 0.00005 (50uA per bit)

		// 5. Compute the calibration register
		// Cal = trunc (0.04096 / (Current_LSB * RSHUNT))
		// Cal = 8192 (0x2000)

		ina219_calValue = 8192;

		// 6. Calculate the power LSB
		// PowerLSB = 20 * CurrentLSB
		// PowerLSB = 0.001 (1mW per bit)

		// 7. Compute the maximum current and shunt voltage values before
		// overflow
		//
		// Max_Current = Current_LSB * 32767
		// Max_Current = 1.63835A before overflow
		//
		// If Max_Current > Max_Possible_I then
		// Max_Current_Before_Overflow = MaxPossible_I
		// Else
		// Max_Current_Before_Overflow = Max_Current
		// End If
		//
		// Max_Current_Before_Overflow = MaxPossible_I
		// Max_Current_Before_Overflow = 0.4
		//
		// Max_ShuntVoltage = Max_Current_Before_Overflow * RSHUNT
		// Max_ShuntVoltage = 0.04V
		//
		// If Max_ShuntVoltage >= VSHUNT_MAX
		// Max_ShuntVoltage_Before_Overflow = VSHUNT_MAX
		// Else
		// Max_ShuntVoltage_Before_Overflow = Max_ShuntVoltage
		// End If
		//
		// Max_ShuntVoltage_Before_Overflow = VSHUNT_MAX
		// Max_ShuntVoltage_Before_Overflow = 0.04V

		// 8. Compute the Maximum Power
		// MaximumPower = Max_Current_Before_Overflow * VBUS_MAX
		// MaximumPower = 0.4 * 16V
		// MaximumPower = 6.4W

		// Set multipliers to convert raw current/power values
		ina219_currentDivider_mA = 20; // Current LSB = 50uA per bit (1000/50 =
										// 20)
		ina219_powerDivider_mW = 1; // Power LSB = 1mW per bit

		// Set Calibration register to 'Cal' calculated above
		wireWriteRegister(TiINA219Register.INA219_REG_CALIBRATION, (int) ina219_calValue);

		// Set Config register to take into account the settings above
		int config = INA219_CONFIG_BVOLTAGERANGE_16V | INA219_CONFIG_GAIN_1_40MV | INA219_CONFIG_BADCRES_12BIT
				| INA219_CONFIG_SADCRES_12BIT_1S_532US | INA219_CONFIG_MODE_SANDBVOLT_CONTINUOUS;
		wireWriteRegister(TiINA219Register.INA219_REG_CONFIG, config);
	}

	/**
	 * Gets the raw bus voltage (16-bit signed integer, so +-32767)
	 * 
	 * @return
	 * @throws IOException
	 */
	public int getBusVoltage_raw() throws IOException {

		this.i2cmObj.read(this.i2cSlaveAddr, TiINA219Register.INA219_REG_BUSVOLTAGE, data, 0, 2);
		int value =  BigBitConverter.ToInt16(data, 0);
		
		// Shift to the right 3 to drop CNVR and OVF and multiply by LSB
		return ((value >> 3) * 4);
	}

	/**
	 * Gets the raw current value (16-bit signed integer, so +-32767)
	 * 
	 * @return
	 * @throws IOException
	 */
	public int getCurrent_raw() throws IOException {

		// Sometimes a sharp load will reset the INA219, which will
		// reset the cal register, meaning CURRENT and POWER will
		// not be available ... avoid this by always setting a cal
		// value even if it's an unfortunate extra step
		wireWriteRegister(TiINA219Register.INA219_REG_CALIBRATION, (int) ina219_calValue);

		// Now we can safely read the CURRENT register!
		this.i2cmObj.read(this.i2cSlaveAddr, TiINA219Register.INA219_REG_CURRENT, data, 0, 2);
		int value =  BigBitConverter.ToInt16(data, 0);
		
		return value;
	}

	/**
	 * Gets the shunt voltage in mV (so +-327mV)
	 * 
	 * @return
	 * @throws IOException
	 */
	public double getShuntVoltage_mV() throws IOException {
		int value = getShuntVoltage_raw();
		return value * 0.01;
	}

	/**
	 * Gets the raw shunt voltage (16-bit signed integer, so +-32767)
	 * 
	 * @return
	 * @throws IOException
	 */
	public int getShuntVoltage_raw() throws IOException {
		this.i2cmObj.read(this.i2cSlaveAddr, TiINA219Register.INA219_REG_SHUNTVOLTAGE, data, 0, 2);
		int value =  BigBitConverter.ToInt16(data, 0);
		
		return value;
	}

	/**
	 * Gets the shunt voltage in volts
	 * 
	 * @return
	 * @throws IOException
	 */
	public double getBusVoltage_V() throws IOException {
		int value = getBusVoltage_raw();
		return value * 0.001;
	}

	/**
	 * Gets the current value in mA, taking into account the config settings and
	 * current LSB
	 * 
	 * @return
	 * @throws IOException
	 */
	public double getCurrent_mA() throws IOException {
		double valueDec = getCurrent_raw();
		valueDec /= ina219_currentDivider_mA;
		return valueDec;
	}



	/**
	 * Sends a single command byte over I2C
	 * 
	 * @param register's
	 *            address
	 * @param value
	 */
	private void wireWriteRegister(int register, int value) throws IOException {
		synchronized (this.i2cmObj) {
			data[0] = (byte) (value >>> 8); // MSB 1st
			data[1] = (byte) (value & 0xff); // LSB 2nd

			i2cmObj.write(this.i2cSlaveAddr, register, data, 0, 2);
		}
	}

}
