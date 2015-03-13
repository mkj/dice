package au.asn.ucc.matt.dice;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;


public class Dice extends ActionBarActivity {

    private int number;
    private String current_words[];
    Diceware8k dw;
    private static final String PREFS_NAME = "dice";
    private static final int DEFAULT_NUMBER = 5;
    private static final int BITS_PER_WORD = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_dice);

        loadPrefs();

        NumberPicker picker = (NumberPicker)findViewById(R.id.word_number);
        picker.setMinValue(1);
        picker.setMaxValue(20);
        picker.setWrapSelectorWheel(false);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldval, int newval) {
                number = newval;
                generate(picker);
            }
        });
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);



        dw = new Diceware8k();

        current_words = new String[0];
        generate(null);
        picker.setValue(number);
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePrefs();
    }

    private void loadPrefs() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        number = settings.getInt("number", DEFAULT_NUMBER);
    }

    private void savePrefs() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("number", number);

        // Commit the edits!
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent about_intent = new Intent(this, AboutActivity.class);
            startActivity(about_intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void generate(View view) {
        TextView word_text = (TextView) findViewById(R.id.word_text);
        current_words = dw.generate(number);
        String text = Diceware8k.join(current_words, "\n");
        word_text.setText(text);
        TextView bits_label = (TextView)findViewById(R.id.bits_label);
        bits_label.setText(BITS_PER_WORD*number + " bits");
    }

    public void copy_words(View view) {
        String text = Diceware8k.join(current_words, " ");
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text",text);
        clipboard.setPrimaryClip(clip);
    }

}
