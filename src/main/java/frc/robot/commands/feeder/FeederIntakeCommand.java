// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.feeder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.feeder.FeederSubsystem;

import static frc.robot.constants.Constants.FeederConstants.*;

/**
 * Command to run the feeder for intaking.
 */
public class FeederIntakeCommand extends Command {

    private final FeederSubsystem feeder;

    public FeederIntakeCommand(FeederSubsystem feeder) {
        this.feeder = feeder;
        addRequirements(feeder);
    }

    @Override
    public void initialize() {
        double voltage = SmartDashboard.getNumber("Feeder/Intaking Voltage", INTAKING_FEEDER_VOLTAGE);
        feeder.setVoltage(voltage);
    }

    @Override
    public void execute() {
    }

    @Override
    public void end(boolean interrupted) {
        feeder.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
