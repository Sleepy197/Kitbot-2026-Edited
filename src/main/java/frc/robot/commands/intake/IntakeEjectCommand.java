// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeSubsystem;

import static frc.robot.constants.Constants.IntakeConstants.*;

/**
 * Command to eject game pieces through the intake (reverse direction).
 */
public class IntakeEjectCommand extends Command {

    private final IntakeSubsystem intake;

    public IntakeEjectCommand(IntakeSubsystem intake) {
        this.intake = intake;
        addRequirements(intake);
    }

    @Override
    public void initialize() {
        double voltage = -1 * SmartDashboard.getNumber("Intake/Intaking Voltage", INTAKING_INTAKE_VOLTAGE);
        intake.setVoltage(voltage);
    }

    @Override
    public void execute() {
    }

    @Override
    public void end(boolean interrupted) {
        intake.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
