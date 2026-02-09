// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.feeder;

import static frc.robot.constants.Constants.FeederConstants.*;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Subsystem for the feeder roller mechanism.
 * Uses simple feedforward (voltage) control.
 */
public class FeederSubsystem extends SubsystemBase {

    private final TalonFX feederRoller;
    private final VoltageOut voltageRequest = new VoltageOut(0);

    public FeederSubsystem() {
        feederRoller = new TalonFX(FEEDER_MOTOR_ID);

        TalonFXConfiguration config = new TalonFXConfiguration();
        config.CurrentLimits.SupplyCurrentLimit = FEEDER_MOTOR_CURRENT_LIMIT;
        config.CurrentLimits.SupplyCurrentLimitEnable = true;
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        feederRoller.getConfigurator().apply(config);

        SmartDashboard.putNumber("Feeder/Intaking Voltage", INTAKING_FEEDER_VOLTAGE);
        SmartDashboard.putNumber("Feeder/Launching Voltage", LAUNCHING_FEEDER_VOLTAGE);
        SmartDashboard.putNumber("Feeder/SpinUp Voltage", SPIN_UP_FEEDER_VOLTAGE);
    }

    /**
     * Set the feeder roller voltage.
     *
     * @param voltage Voltage to apply (-12 to 12)
     */
    public void setVoltage(double voltage) {
        feederRoller.setControl(voltageRequest.withOutput(voltage));
    }

    /**
     * Stop the feeder roller.
     */
    public void stop() {
        feederRoller.setControl(voltageRequest.withOutput(0));
    }

    @Override
    public void periodic() {
        // telemetry if needed
    }
}
