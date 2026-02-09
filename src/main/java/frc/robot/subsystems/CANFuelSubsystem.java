// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import static frc.robot.constants.Constants.FuelConstants.*;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class CANFuelSubsystem extends SubsystemBase {

    private final TalonFX feederRoller;
    private final TalonFX intakeLauncherRoller;

    // Control requests for voltage control
    private final VoltageOut feederVoltageRequest = new VoltageOut(0);
    private final VoltageOut launcherVoltageRequest = new VoltageOut(0);

    /** Creates a new CANBallSubsystem. */
    public CANFuelSubsystem() {
        // create TalonFX motors for each of the motors on the launcher mechanism
        intakeLauncherRoller = new TalonFX(INTAKE_LAUNCHER_MOTOR_ID);
        feederRoller = new TalonFX(FEEDER_MOTOR_ID);

        // create the configuration for the feeder roller, set a current limit and apply
        // the config to the controller
        TalonFXConfiguration feederConfig = new TalonFXConfiguration();
        feederConfig.CurrentLimits.SupplyCurrentLimit = FEEDER_MOTOR_CURRENT_LIMIT;
        feederConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        feederConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        feederRoller.getConfigurator().apply(feederConfig);

        // create the configuration for the launcher roller, set a current limit, set
        // the motor to inverted so that positive values are used for both intaking and
        // launching, and apply the config to the controller
        TalonFXConfiguration launcherConfig = new TalonFXConfiguration();
        launcherConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        launcherConfig.CurrentLimits.SupplyCurrentLimit = LAUNCHER_MOTOR_CURRENT_LIMIT;
        launcherConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        launcherConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        intakeLauncherRoller.getConfigurator().apply(launcherConfig);

        // put default values for various fuel operations onto the dashboard
        // all commands using this subsystem pull values from the dashbaord to allow
        // you to tune the values easily, and then replace the values in Constants.java
        // with your new values. For more information, see the Software Guide.
        SmartDashboard.putNumber(
                "Intaking feeder roller value",
                INTAKING_FEEDER_VOLTAGE);
        SmartDashboard.putNumber(
                "Intaking intake roller value",
                INTAKING_INTAKE_VOLTAGE);
        SmartDashboard.putNumber(
                "Launching feeder roller value",
                LAUNCHING_FEEDER_VOLTAGE);
        SmartDashboard.putNumber(
                "Launching launcher roller value",
                LAUNCHING_LAUNCHER_VOLTAGE);
        SmartDashboard.putNumber(
                "Spin-up feeder roller value",
                SPIN_UP_FEEDER_VOLTAGE);
    }

    // A method to set the voltage of the intake launcher roller
    public void setIntakeLauncherRoller(double voltage) {
        intakeLauncherRoller.setControl(launcherVoltageRequest.withOutput(voltage));
    }

    // A method to set the voltage of the feeder roller
    public void setFeederRoller(double voltage) {
        feederRoller.setControl(feederVoltageRequest.withOutput(voltage));
    }

    // A method to stop the rollers
    public void stop() {
        feederRoller.setControl(feederVoltageRequest.withOutput(0));
        intakeLauncherRoller.setControl(launcherVoltageRequest.withOutput(0));
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }
}
