package frc.Mechanisms;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

public class CatzArm
{
    private WPI_TalonFX armMtr;

    private final int ARM_MC_ID = 20;

    private final double EXTEND_PWR  = 0.2;
    private final double RETRACT_PWR = -0.2;


    //current limiting
    private SupplyCurrentLimitConfiguration armCurrentLimit;
    private final int     CURRENT_LIMIT_AMPS            = 55;
    private final int     CURRENT_LIMIT_TRIGGER_AMPS    = 55;
    private final double  CURRENT_LIMIT_TIMEOUT_SECONDS = 0.5;
    private final boolean ENABLE_CURRENT_LIMIT          = true;


    public CatzArm()
    {
        armMtr = new WPI_TalonFX(ARM_MC_ID);

        armMtr.configFactoryDefault();

        armMtr.setNeutralMode(NeutralMode.Brake);
        armMtr.configForwardSoftLimitEnable(true);
        armMtr.configReverseSoftLimitEnable(true);
        armMtr.configForwardSoftLimitThreshold(43000.0);
        armMtr.configReverseSoftLimitThreshold(1000.0);

        armCurrentLimit = new SupplyCurrentLimitConfiguration(ENABLE_CURRENT_LIMIT, CURRENT_LIMIT_AMPS, CURRENT_LIMIT_TRIGGER_AMPS, CURRENT_LIMIT_TIMEOUT_SECONDS);

        armMtr.configSupplyCurrentLimit(armCurrentLimit);

    }

    public void cmdProcArm(boolean armExtend, boolean armRetract,
                            double gamePiece, boolean lowNode, boolean midNode, boolean highNode, boolean stowPos,
                            boolean pickUpPos)
    {
        if(armExtend)
        {
            setArmPwr(EXTEND_PWR);
        }
        else if(armRetract)
        {
            setArmPwr(RETRACT_PWR);
        }
        else
        {
            setArmPwr(0.0);
        }
    }

    public void setArmPwr(double pwr)
    {        
        armMtr.set(ControlMode.PercentOutput, pwr);
    }
    
}
