package frc.Mechanisms;

import java.util.MissingFormatWidthException;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CatzElevator
{
    private WPI_TalonFX elevatorMtr;

    private final int ELEVATOR_MC_ID = 10;

    private final double MAX_POWER = 0.3;

    private double mtrPower;
    private boolean manualMode;

    //current limiting
    private SupplyCurrentLimitConfiguration elevatorCurrentLimit;
    private final int     CURRENT_LIMIT_AMPS            = 55;
    private final int     CURRENT_LIMIT_TRIGGER_AMPS    = 55;
    private final double  CURRENT_LIMIT_TIMEOUT_SECONDS = 0.5;
    private final boolean ENABLE_CURRENT_LIMIT          = true;


    private DigitalInput lowLimitSwitch;
    private DigitalInput midLimitSwitch;
    private DigitalInput highLimitSwitch;

    private final int LOW_LIMIT_SWITCH_PORT  = 0;
    private final int MID_LIMIT_SWITCH_PORT  = 1;
    private final int HIGH_LIMIT_SWITCH_PORT = 2;

    private final double LOW_POS_ENC_CNTS  = 0.0;
    private final double MID_POS_ENC_CNTS  = 88500.0;
    private final double HIGH_POS_ENC_CNTS = 111200.0;

    private final boolean PRESSED     = false;
    private final boolean NOT_PRESSED = true;


    public CatzElevator()
    {
        elevatorMtr = new WPI_TalonFX(ELEVATOR_MC_ID);

        elevatorMtr.configFactoryDefault();

        elevatorMtr.setNeutralMode(NeutralMode.Brake);
        elevatorMtr.configForwardSoftLimitEnable(true);
        elevatorMtr.configReverseSoftLimitEnable(true);
        elevatorMtr.configForwardSoftLimitThreshold(111500.0);
        elevatorMtr.configReverseSoftLimitThreshold(0.0);

        elevatorCurrentLimit = new SupplyCurrentLimitConfiguration(ENABLE_CURRENT_LIMIT, CURRENT_LIMIT_AMPS, CURRENT_LIMIT_TRIGGER_AMPS, CURRENT_LIMIT_TIMEOUT_SECONDS);

        elevatorMtr.configSupplyCurrentLimit(elevatorCurrentLimit);


        lowLimitSwitch = new DigitalInput(LOW_LIMIT_SWITCH_PORT);
        midLimitSwitch = new DigitalInput(MID_LIMIT_SWITCH_PORT);
        highLimitSwitch = new DigitalInput(HIGH_LIMIT_SWITCH_PORT);

        elevatorMtr.config_kP(0, 0.035);
        elevatorMtr.config_kI(0, 0.0001);
        elevatorMtr.config_kD(0, 0.001);
        //elevatorMtr.configMaxIntegralAccumulator(0, 0.2);
        elevatorMtr.config_IntegralZone(0, 2000.0);

        elevatorMtr.selectProfileSlot(0, 0);

        elevatorMtr.configMotionCruiseVelocity(20000.0);
        elevatorMtr.configMotionAcceleration(40000.0);
        elevatorMtr.configMotionSCurveStrength(2);

        elevatorMtr.configAllowableClosedloopError(0, 200.0);

    }

    public void cmdProcElevator(double elevatorPwr, boolean ignoreSoftLimits, 
                                double gamePiece, boolean lowNode, boolean midNode, boolean highNode, boolean stowPos,
                                boolean pickUpPos)
    {
        if(Math.abs(elevatorPwr) > 0.1 || manualMode == true)
        {
            manualMode = true;
            elevatorManual(elevatorPwr);

            if(Math.abs(elevatorPwr) < 0.1)
            {
                manualMode = false;
                setElevatorPower(0.0);
            }
        }

        if(ignoreSoftLimits)
        {
            elevatorMtr.configForwardSoftLimitEnable(false);
            elevatorMtr.configReverseSoftLimitEnable(false);
        }
        else
        {
            elevatorMtr.configForwardSoftLimitEnable(true);
            elevatorMtr.configReverseSoftLimitEnable(true);
        }


        if(lowNode)
        {
            elevatorLowPos();
        }
        else if(midNode)
        {
            elevatorMidPos();
        }
        else if(highNode)
        {
            elevatorHighPos();
        }
        
    }
    

    public void elevatorManual(double pwr)
    {
        mtrPower = pwr * MAX_POWER;

        setElevatorPower(mtrPower);
    }

    public void setElevatorPower(double pwr)
    {
        if(((highLimitSwitch.get() == PRESSED) && pwr > 0.0) || ((lowLimitSwitch.get() == PRESSED) && pwr < 0.0))
        {
            elevatorMtr.set(ControlMode.PercentOutput, 0.0);
        }
        else
        {
            elevatorMtr.set(ControlMode.PercentOutput, pwr);
        }
    }

    public void elevatorLowPos()
    {
        elevatorMtr.set(ControlMode.Position, LOW_POS_ENC_CNTS);
    }

    public void elevatorMidPos()
    {
        elevatorMtr.set(ControlMode.Position, MID_POS_ENC_CNTS);
    }

    public void elevatorHighPos()
    {
        elevatorMtr.set(ControlMode.Position, HIGH_POS_ENC_CNTS);
    }
    


    public void checkLimitSwitches()
    {
        if(lowLimitSwitch.get() == PRESSED)
        {
            elevatorMtr.setSelectedSensorPosition(LOW_POS_ENC_CNTS);
        }
        if(midLimitSwitch.get() == PRESSED)
        {
           elevatorMtr.setSelectedSensorPosition(MID_POS_ENC_CNTS);
        }
        if(highLimitSwitch.get() == PRESSED)
        {
            elevatorMtr.setSelectedSensorPosition(HIGH_POS_ENC_CNTS);
        }
    }


    public void shuffleboardElevator()
    {
        SmartDashboard.putBoolean("Low Limit Switch", !lowLimitSwitch.get());
        SmartDashboard.putBoolean("Mid Limit Switch", !midLimitSwitch.get());
        SmartDashboard.putBoolean("High Limit Switch", !highLimitSwitch.get());
    }

    public void shuffleboardElevator_DEBUG()
    {
        SmartDashboard.putNumber("Elevator Enc Pos", elevatorMtr.getSelectedSensorPosition());
        SmartDashboard.putNumber("Elev Closed Loop Error", elevatorMtr.getClosedLoopError());
        SmartDashboard.putNumber("kI Accumulation", elevatorMtr.getIntegralAccumulator());
    }
}
