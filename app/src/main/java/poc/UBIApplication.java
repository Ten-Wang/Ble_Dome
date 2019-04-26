package poc;


import androidx.multidex.MultiDexApplication;

public class UBIApplication extends MultiDexApplication {
    boolean demoActivityActive = false;

    public boolean isDemoActivityActive() {
        return demoActivityActive;
    }

    public void setDemoActivityActive(boolean demoActivityActive) {
        this.demoActivityActive = demoActivityActive;
    }


}
