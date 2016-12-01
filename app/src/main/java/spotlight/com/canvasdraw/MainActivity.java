package spotlight.com.canvasdraw;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import spotlight.com.canvasdraw.Unity.CanvasView;


public class MainActivity extends Activity {
    String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/CanvasDraw/";
    CanvasView canvas = null;
    ImageView rlIcon1,rlIcon2,rlIcon3,rlIcon4,rlIcon5,rlIcon6;
    int currentPencilBreadth=0,currentBackgroundColor = 0xffffffff;
    final int DEFAULT_THUMB_COLOR = 0xff009688;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // delete title
        setContentView(R.layout.activity_main);
        canvas = (CanvasView)this.findViewById(R.id.canvas);
        setActionButton();
        setOnclick();
    }

    public void setActionButton(){
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.icon_action_button));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();
        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        rlIcon1 = new ImageView(this);
        rlIcon2 = new ImageView(this);
        rlIcon3 = new ImageView(this);
        rlIcon4 = new ImageView(this);
        rlIcon5 = new ImageView(this);
        rlIcon6 = new ImageView(this);
        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.icon_undo));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.icon_redo));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.icon_width));
        rlIcon4.setImageDrawable(getResources().getDrawable(R.drawable.icon_color));
        rlIcon5.setImageDrawable(getResources().getDrawable(R.drawable.icon_clear));
        rlIcon6.setImageDrawable(getResources().getDrawable(R.drawable.icon_save));
        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon4).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon5).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon6).build())
                .setStartAngle(160)
                .setEndAngle(290)
                .attachTo(rightLowerButton)
                .build();
        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });
    }

    public void setOnclick(){
        rlIcon1.setOnClickListener(new View.OnClickListener() { //undo
            @Override
            public void onClick(View v) {
                canvas.undo();
            }
        });
        rlIcon2.setOnClickListener(new View.OnClickListener() { //redo
            @Override
            public void onClick(View v) {
                canvas.redo();
            }
        });
        rlIcon3.setOnClickListener(new View.OnClickListener() { //width
            @Override
            public void onClick(View v) {
                PencelWidth();
            }
        });

        rlIcon4.setOnClickListener(new View.OnClickListener() { //color
            @Override
            public void onClick(View v) {
                ColorPickerDialog();
            }
        });

        rlIcon5.setOnClickListener(new View.OnClickListener() { // clear
            @Override
            public void onClick(View v) {
                canvas.clear();
            }
        });
        rlIcon6.setOnClickListener(new View.OnClickListener() { //save
            @Override
            public void onClick(View v) {
                saveCanvas();
            }
        });
    }
    public void PencelWidth(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pencil_width);

        final DiscreteSeekBar seekBar = (DiscreteSeekBar)dialog.findViewById(R.id.breadth);
        seekBar.setMin(0);
        seekBar.setMax(50);
        seekBar.setProgress(currentPencilBreadth);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setTextColor(DEFAULT_THUMB_COLOR);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button ok = (Button) dialog.findViewById(R.id.ok);
        ok.setTextColor(DEFAULT_THUMB_COLOR);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                currentPencilBreadth = seekBar.getProgress();
                Toast.makeText(MainActivity.this, "寬度為: " + currentPencilBreadth , Toast.LENGTH_LONG).show();
                canvas.setPaintStrokeWidth(currentPencilBreadth);
            }
        });
        // 調整大小
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        dialog.show();

        dialog.getWindow().setAttributes(lp);
    }
    public void ColorPickerDialog(){
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(currentBackgroundColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        currentBackgroundColor = selectedColor;
                        Toast.makeText(MainActivity.this, "選擇的顏色: 0x" + Integer.toHexString(currentBackgroundColor), Toast.LENGTH_LONG).show();
                        canvas.setPaintStrokeColor(currentBackgroundColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    public void saveCanvas(){
        mkdir();
        int error=0;
        Bitmap bitmap = canvas.getBitmap();
        byte[] bytes = CanvasView.getBitmapAsByteArray(bitmap, Bitmap.CompressFormat.PNG, 100);
        Calendar c = Calendar.getInstance();
        String filename = c.getTimeInMillis()+".png";
        File photo=new File(DATA_PATH , filename);
        if (photo.exists()) {
            photo.delete();
        }
        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());
            fos.write(bytes);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
            error=1;
        }
        if(error==0)
            Toast.makeText(MainActivity.this,"儲存成功。",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this,"儲存失敗。",Toast.LENGTH_SHORT).show();

    }

    public void mkdir()
    {
        String[] paths = new String[] { DATA_PATH };
        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v("sd", "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v("sd", "Created directory " + path + " on sdcar");
                }
            }

        }
    }
}
