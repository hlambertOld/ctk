package context.arch.generator;

/**
 * This class is a container for temperature data.  This includes the temperature
 * information which consists of degrees and units and timestamp.
 */
public class SensorData { 

  private String degrees;
  private String units;
  private String time;
  private String status;
 
  public SensorData() {}
 
  /**
   * Basic constructor
   *
   * @param degrees Degrees of the temperature
   * @param units Units the temperature is measured in
   * @param Time Timestamp for the data
   */
  public SensorData(String degrees, String units, String time, String status){
    this.degrees = degrees;
    this.units = units;
    this.time = time;
    this.status = status;
  }

  /**
   * Returns the degrees of the temperature
   *
   * @return the degrees of the temperature
   */
  public String getDegrees(){
    return degrees;
  }

  /**
   * Returns the units the temperature is measured in 
   *
   * @return the units the temperature is measured in 
   */
  public String getUnits(){
    return units;
  }

  /**
   * Sets the degrees of the temperature
   *
   * @param degrees the degrees of the temperature
   */
  public void setDegrees(String degrees){
    this.degrees = degrees;
  }

  /**
   * Sets the units the temperature is measured in 
   *
   * @param units the type the temperature is measured in 
   */
  public void setUnits(String units){
    this.units = units;
  }

  /**
   * Returns the timestamp 
   *
   * @return the timestamp
   */
  public String getTime(){
    return time;
  }

  /**
   * Sets the timestamp
   *
   * @param time the timestamp
   */
  public void setTime(String time){
    this.time = time;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}

