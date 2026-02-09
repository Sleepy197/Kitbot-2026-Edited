// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake;

import static frc.robot.constants.Constants.IntakeConstants.*;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Subsystem for the intake/launcher roller mechanism.
 * Uses simple feedforward (voltage) control.
 */
public class IntakeSubsystem extends SubsystemBase {

    private final TalonFX intakeRoller;
    private final VoltageOut voltageRequest = new VoltageOut(0);

    public IntakeSubsystem() {
        intakeRoller = new TalonFX(INTAKE_MOTOR_ID);

        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        config.CurrentLimits.SupplyCurrentLimit = INTAKE_MOTOR_CURRENT_LIMIT;
        config.CurrentLimits.SupplyCurrentLimitEnable = true;
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        intakeRoller.getConfigurator().apply(config);

        SmartDashboard.putNumber("Intake/Intaking Voltage", INTAKING_INTAKE_VOLTAGE);
        SmartDashboard.putNumber("Intake/Launching Voltage", LAUNCHING_INTAKE_VOLTAGE);
    }

    /**
     * Set the intake roller voltage.
     *
     * @param voltage Voltage to apply (-12 to 12)
     */
    public void setVoltage(double voltage) {
        intakeRoller.setControl(voltageRequest.withOutput(voltage));
    }

    /**
     * Stop the intake roller.
     */
    public void stop() {
        intakeRoller.setControl(voltageRequest.withOutput(0));
    }

    @Override
    public void periodic() {
        // telemetry if needed
    }
}
