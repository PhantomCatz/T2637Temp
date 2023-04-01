package frc.Mechanisms;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.CANCoder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CatzIntake
{
    private WPI_TalonFX wristMtr;
    private WPI_TalonFX rollersMtr;

    private final int WRIST_MC_ID   = 31;
    private final int ROLLERS_MC_ID = 30;

    private final double WRIST_MAX_PWR = 0.4;
    private final double ROLLERS_PWR   = 0.4;

    private double mtrPower;

    private CANCoder wristEncoder;
    private final int WRIST_ENC_CAN_ID = 13; 


    //current limiting
    private SupplyCurrentLimitConfiguration wristCurrentLimit;
    private final int     CURRENT_LIMIT_AMPS            = 55;
    private final int     CURRENT_LIMIT_TRIGGER_AMPS    = 55;
    private final double  CURRENT_LIMIT_TIMEOUT_SECONDS = 0.5;
    private final boolean ENABLE_CURRENT_LIMIT          = true;


    public CatzIntake()
    {
        wristMtr = new WPI_TalonFX(WRIST_MC_ID);

        wristMtr.configFactoryDefault();

        wristMtr.setNeutralMode(NeutralMode.Brake);
        wristMtr.configForwardSoftLimitEnable(true);
        wristMtr.configReverseSoftLimitEnable(true);
        wristMtr.configForwardSoftLimitThreshold(2500);
        wristMtr.configReverseSoftLimitThreshold(100.0);

        wristCurrentLimit = new SupplyCurrentLimitConfiguration(ENABLE_CURRENT_LIMIT, CURRENT_LIMIT_AMPS, CURRENT_LIMIT_TRIGGER_AMPS, CURRENT_LIMIT_TIMEOUT_SECONDS);

        wristMtr.configSupplyCurrentLimit(wristCurrentLimit);

        wristEncoder = new CANCoder(WRIST_ENC_CAN_ID);


        rollersMtr = new WPI_TalonFX(ROLLERS_MC_ID);

        rollersMtr.configFactoryDefault();

        rollersMtr.setNeutralMode(NeutralMode.Coast);
        
        rollersMtr.configSupplyCurrentLimit(wristCurrentLimit);


    }

    public void cmdProcIntake(double wristPwr, boolean ignoreSoftLimits, boolean rollersIn, boolean rollersOut,
                              double gamePiece, boolean lowNode, boolean midNode, boolean highNode, boolean stowPos,
                              boolean pickUpPos)
    {
        
        if(Math.abs(wristPwr) >= 0.1)
        {
            wristManual(wristPwr);
        }
        else
        {
            wristManual(0.0);
        }

        if(ignoreSoftLimits)
        {
            wristMtr.configForwardSoftLimitEnable(false);
            wristMtr.configReverseSoftLimitEnable(false);
        }
        else
        {
            wristMtr.configForwardSoftLimitEnable(true);
            wristMtr.configReverseSoftLimitEnable(true);
        }

        if(rollersIn)
        {
            rollersIn();
        }
        else if(rollersOut)
        {
            rollersOut();
        }
        else
        {
            rollersOff();
        }
    }

    public void wristManual(double pwr)
    {
        mtrPower = pwr * WRIST_MAX_PWR;
        
        wristMtr.set(ControlMode.PercentOutput, mtrPower);
        System.out.println(mtrPower);
    }

    public void rollersIn()
    {
        rollersMtr.set(ControlMode.PercentOutput, ROLLERS_PWR);
    }

    public void rollersOut()
    {
        rollersMtr.set(ControlMode.PercentOutput, -ROLLERS_PWR);
    }

    public void rollersOff()
    {
        rollersMtr.set(ControlMode.PercentOutput, 0.0);
    }
    
    public void shuffleboardIntake()
    {

    }

    public void shuffleboardIntakeDebug()
    {
        SmartDashboard.putNumber("Abs Pos Wrist Enc", wristEncoder.getAbsolutePosition());
        
    }
}
