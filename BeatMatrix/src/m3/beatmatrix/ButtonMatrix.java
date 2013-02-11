package m3.beatmatrix;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ButtonMatrix extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_matrix);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_button_matrix, menu);
        return true;
    }
    
}
