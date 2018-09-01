package nl.kleisauke.compactcalendarviewtoolbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Scratch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SignatureMainLayout(this));
    }
}
