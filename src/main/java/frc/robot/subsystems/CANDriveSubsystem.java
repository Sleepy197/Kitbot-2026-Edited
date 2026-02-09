// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.Constants.DriveConstants.*;

public class CANDriveSubsystem extends SubsystemBase {
  private final WPI_TalonSRX leftLeader;
  private final WPI_TalonSRX leftFollower;
  private final WPI_TalonSRX rightLeader;
  private final WPI_TalonSRX rightFollower;

  private final DifferentialDrive drive;

  public CANDriveSubsystem() {
    // Create TalonSRX motors for drive
    leftLeader = new WPI_TalonSRX(LEFT_LEADER_ID);
    leftFollower = new WPI_TalonSRX(LEFT_FOLLOWER_ID);
    rightLeader = new WPI_TalonSRX(RIGHT_LEADER_ID);
    rightFollower = new WPI_TalonSRX(RIGHT_FOLLOWER_ID);

    // Factory default all motors to ensure clean configuration
    leftLeader.configFactoryDefault();
    leftFollower.configFactoryDefault();
    rightLeader.configFactoryDefault();
    rightFollower.configFactoryDefault();

    // Set neutral mode to brake
    leftLeader.setNeutralMode(NeutralMode.Brake);
    leftFollower.setNeutralMode(NeutralMode.Brake);
    rightLeader.setNeutralMode(NeutralMode.Brake);
    rightFollower.setNeutralMode(NeutralMode.Brake);

    // Configure current limits to prevent tripping breakers or damaging motors
    leftLeader.configContinuousCurrentLimit(DRIVE_MOTOR_CURRENT_LIMIT);
    leftLeader.configPeakCurrentLimit(DRIVE_MOTOR_CURRENT_LIMIT);
    leftLeader.enableCurrentLimit(false);

    leftFollower.configContinuousCurrentLimit(DRIVE_MOTOR_CURRENT_LIMIT);
    leftFollower.configPeakCurrentLimit(DRIVE_MOTOR_CURRENT_LIMIT);
    leftFollower.enableCurrentLimit(false);

    rightLeader.configContinuousCurrentLimit(DRIVE_MOTOR_CURRENT_LIMIT);
    rightLeader.configPeakCurrentLimit(DRIVE_MOTOR_CURRENT_LIMIT);
    rightLeader.enableCurrentLimit(false);

    rightFollower.configContinuousCurrentLimit(DRIVE_MOTOR_CURRENT_LIMIT);
    rightFollower.configPeakCurrentLimit(DRIVE_MOTOR_CURRENT_LIMIT);
    rightFollower.enableCurrentLimit(false);

    // Configure voltage compensation
    leftLeader.configVoltageCompSaturation(12);
    leftLeader.enableVoltageCompensation(true);
    rightLeader.configVoltageCompSaturation(12);
    rightLeader.enableVoltageCompensation(true);

    // Set followers to follow their respective leaders
    leftFollower.follow(leftLeader);
    rightFollower.follow(rightLeader);

    // Invert left side so that positive values drive both sides forward
    leftLeader.setInverted(false);
    leftFollower.setInverted(false);

    // Set up differential drive class
    drive = new DifferentialDrive(leftLeader, rightLeader);
  }

  @Override
  public void periodic() {
  }

  public void driveArcade(double xSpeed, double zRotation) {
    drive.arcadeDrive(xSpeed, zRotation);
  }

}
