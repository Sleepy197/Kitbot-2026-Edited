// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;
import static frc.robot.constants.Constants.FeederConstants.*;
import static frc.robot.constants.Constants.OperatorConstants.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.commands.tank.CommandSwerveDrivetrain;
import frc.robot.commands.feeder.*;
import frc.robot.commands.intake.*;
import frc.robot.constants.TunerConstants;
import frc.robot.subsystems.feeder.FeederSubsystem;
import frc.robot.subsystems.intake.IntakeSubsystem;

public class RobotContainer {

    private double MaxSpeed = 1.0 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top
                                                                                        // speed
    private double MaxAngularRate = RotationsPerSecond.of(0.4).in(
            RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1)
            .withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController joystick = new CommandXboxController(0);

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    // Modular subsystems
    private final FeederSubsystem feederSubsystem = new FeederSubsystem();
    private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();

    private final CommandXboxController operatorController = new CommandXboxController(OPERATOR_CONTROLLER_PORT);

    public RobotContainer() {
        configureBindings();
    }

    private void configureBindings() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
                // Drivetrain will execute this command periodically
                drivetrain.applyRequest(
                        () -> drive
                                .withVelocityX(-0.3 * joystick.getLeftY() * MaxSpeed) // Drive forward with negative Y
                                                                                      // (forward)
                                .withVelocityY(0.3 * joystick.getLeftX() * MaxSpeed) // Drive left with negative X
                                                                                     // (left)
                                .withRotationalRate(
                                        joystick.getRightX() * MaxAngularRate) // Drive counterclockwise with negative X
                                                                               // (left)
                ));

        // Idle while the robot is disabled. This ensures the configured
        // neutral mode is applied to the drive motors while disabled.
        final var idle = new SwerveRequest.Idle();
        RobotModeTriggers.disabled().whileTrue(
                drivetrain.applyRequest(() -> idle).ignoringDisable(true));

        joystick.a().whileTrue(drivetrain.applyRequest(() -> brake));
        joystick
                .b()
                .whileTrue(
                        drivetrain.applyRequest(() -> point.withModuleDirection(
                                new Rotation2d(
                                        -joystick.getLeftY(),
                                        -joystick.getLeftX()))));

        // Run SysId routines when holding back/start and X/Y.
        // Note that each routine should be run exactly once in a single log.
        joystick
                .back()
                .and(joystick.y())
                .whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        joystick
                .back()
                .and(joystick.x())
                .whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        joystick
                .start()
                .and(joystick.y())
                .whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        joystick
                .start()
                .and(joystick.x())
                .whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        // Reset the field-centric heading on left bumper press.
        joystick
                .leftBumper()
                .onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

        drivetrain.registerTelemetry(logger::telemeterize);

        // Operator controls using modular subsystems
        // Intake: Run both feeder and intake in parallel
        operatorController.leftBumper().whileTrue(
                new ParallelCommandGroup(
                        new FeederIntakeCommand(feederSubsystem),
                        new IntakeInCommand(intakeSubsystem)));

        // Launch sequence: Spin up both, then launch both
        SequentialCommandGroup launchSequence = new SequentialCommandGroup(
                new ParallelCommandGroup(
                        new FeederSpinUpCommand(feederSubsystem),
                        new IntakeLaunchCommand(intakeSubsystem)).withTimeout(SPIN_UP_SECONDS),
                new ParallelCommandGroup(
                        new FeederLaunchCommand(feederSubsystem),
                        new IntakeLaunchCommand(intakeSubsystem)));
        operatorController.rightBumper().whileTrue(launchSequence);

        // Eject: Run both in reverse
        operatorController.a().whileTrue(
                new ParallelCommandGroup(
                        new FeederEjectCommand(feederSubsystem),
                        new IntakeEjectCommand(intakeSubsystem)));
    }

    public Command getAutonomousCommand() {
        // Simple drive forward auton
        final var idle = new SwerveRequest.Idle();
        return Commands.sequence(
                // Reset our field centric heading to match the robot
                // facing away from our alliance station wall (0 deg).
                drivetrain.runOnce(() -> drivetrain.seedFieldCentric(Rotation2d.kZero)),
                // Then slowly drive forward (away from us) for 5 seconds.
                drivetrain
                        .applyRequest(() -> drive
                                .withVelocityX(0.5)
                                .withVelocityY(0)
                                .withRotationalRate(0))
                        .withTimeout(5.0),
                // Finally idle for the rest of auton
                drivetrain.applyRequest(() -> idle));
    }
}
