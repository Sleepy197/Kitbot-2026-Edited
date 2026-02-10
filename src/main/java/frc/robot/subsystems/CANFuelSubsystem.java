// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.Constants.FuelConstants.*;

public class CANFuelSubsystem extends SubsystemBase {
  private final WPI_TalonSRX feederRoller;
  private final WPI_TalonSRX intakeLauncherRoller;

  /** Creates a new CANFuelSubsystem. */
  public CANFuelSubsystem() {
    // Create TalonSRX motors for the fuel mechanism
    intakeLauncherRoller = new WPI_TalonSRX(INTAKE_LAUNCHER_MOTOR_ID);
    feederRoller = new WPI_TalonSRX(FEEDER_MOTOR_ID);

    // Factory default to ensure clean configuration
    feederRoller.configFactoryDefault();
    intakeLauncherRoller.configFactoryDefault();

    // Set neutral mode to brake
    feederRoller.setNeutralMode(NeutralMode.Brake);
    intakeLauncherRoller.setNeutralMode(NeutralMode.Brake);

    // Configure current limit for the feeder roller
    feederRoller.configContinuousCurrentLimit(FEEDER_MOTOR_CURRENT_LIMIT);
    feederRoller.configPeakCurrentLimit(FEEDER_MOTOR_CURRENT_LIMIT);
    feederRoller.enableCurrentLimit(false);
    feederRoller.setInverted(true);

    // Configure current limit for the launcher roller and invert it
    // so that positive values are used for both intaking and launching
    intakeLauncherRoller.configContinuousCurrentLimit(LAUNCHER_MOTOR_CURRENT_LIMIT);
    intakeLauncherRoller.configPeakCurrentLimit(LAUNCHER_MOTOR_CURRENT_LIMIT);
    intakeLauncherRoller.enableCurrentLimit(false);
    intakeLauncherRoller.setInverted(true);

    // Put default values for various fuel operations onto the dashboard
    // All commands using this subsystem pull values from the dashboard to allow
    // you to tune the values easily, and then replace the values in Constants.java
    // with your new values. For more information, see the Software Guide.
    SmartDashboard.putNumber("Intaking feeder roller value", INTAKING_FEEDER_VOLTAGE);
    SmartDashboard.putNumber("Intaking intake roller value", INTAKING_INTAKE_VOLTAGE);
    SmartDashboard.putNumber("Launching feeder roller value", LAUNCHING_FEEDER_VOLTAGE);
    SmartDashboard.putNumber("Launching launcher roller value", LAUNCHING_LAUNCHER_VOLTAGE);
    SmartDashboard.putNumber("Spin-up feeder roller value", SPIN_UP_FEEDER_VOLTAGE);
  }

  // A method to set the voltage of the intake roller
  public void setIntakeLauncherRoller(double voltage) {
    intakeLauncherRoller.setVoltage(voltage);
  }

  // A method to set the voltage of the feeder roller
  public void setFeederRoller(double voltage) {
    feederRoller.setVoltage(voltage);
  }

  // A method to stop the rollers
  public void stop() {
    feederRoller.set(0);
    intakeLauncherRoller.set(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
