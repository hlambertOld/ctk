package context.arch.generator;

/**
 * Indeholder data for rumsensoren. Motion & timestamp.
 */
public class SensorData { 

	private String motion;
	private String time;

	public SensorData() {}


	public SensorData(String time, String motion){
		this.motion = motion;
		this.time = time;
	}

	public String getTime(){
		return time;
	}

	public void setTime(String time){
		this.time = time;
	}

	public String getMotion() {
		return motion;
	}
	
	public void setMotion(String motion){
		this.motion = motion;
	}

}