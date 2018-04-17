package lczq.teatprogress;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private PercentView percentView;
    private int aimPercent=85;
    private LinearLayout layout;
    private LinearLayout layout2;
    AnimatorSet set;
    AnimatorSet set2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usercard_activity);
        layout= (LinearLayout) findViewById(R.id.lineatLayout);
        layout2= (LinearLayout) findViewById(R.id.lineatLayout2);

        percentView= (PercentView) findViewById(R.id.percent_view);
        percentView.setAngel(aimPercent);
        percentView.setRankText("名列前茅", "120");


        ObjectAnimator cardA;
        ObjectAnimator cardA1;
        ObjectAnimator cardZ;
        ObjectAnimator cardZ1;
        ObjectAnimator cardF;
        ObjectAnimator cardF1;

        cardZ=ObjectAnimator.ofFloat(layout, "alpha", 1f, 0f).setDuration(2000);
        cardA = ObjectAnimator.ofFloat(layout,"rotationY",0.0F, 180.0F).setDuration(2000);
        cardF=ObjectAnimator.ofFloat(layout2, "alpha", 0f, 1f).setDuration(5000);

        set = new AnimatorSet();
        set2 = new AnimatorSet();
        set.playTogether(cardZ,cardA,cardF);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
//                layout2.setVisibility(View.INVISIBLE);
//                layout2.setEnabled(false);
                animalEnd=false;
                if(state==0){
                    ObjectAnimator.ofFloat(layout2,"rotationY",180.0F, 0.0F).start();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                layout2.setVisibility(View.VISIBLE);
//                layout2.setEnabled(false);
//                ObjectAnimator.ofFloat(layout2, "alpha", 0f, 1f).setDuration(200).start();
                animalEnd=true;
                state=1;
            }
        });


        cardZ1=ObjectAnimator.ofFloat(layout2, "alpha", 1f, 0f).setDuration(2000);
        cardA1 = ObjectAnimator.ofFloat(layout2,"rotationY",0.0F, 180.0F).setDuration(2000);
        cardF1=ObjectAnimator.ofFloat(layout, "alpha", 0f, 1f).setDuration(5000);
        set2.playTogether(cardA1,cardF1,cardZ1);
        set2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
//                layout2.setVisibility(View.INVISIBLE);
//                layout.setEnabled(false);
                if(state==1){
                    ObjectAnimator.ofFloat(layout,"rotationY",180.0F, 0.0F).start();
                }

                animalEnd=false;

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                layout2.setVisibility(View.VISIBLE);
//                ObjectAnimator.ofFloat(layout2, "alpha", 0f, 1f).setDuration(200).start();
//                layout.setEnabled(true);
                animalEnd=true;
                state=0;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        aimPercent=0;

    }

    private int state=0;
    private boolean animalEnd=true;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lineatLayout:
                if(animalEnd){
                    if(state==0){
                        //正---反
                        set.start();
                    }else {
                        set2.start();
                    }
                }
                break;
            case R.id.lineatLayout2:

                break;
        }
    }
}
