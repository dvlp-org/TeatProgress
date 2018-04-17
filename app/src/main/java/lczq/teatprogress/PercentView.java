
package lczq.teatprogress;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


/**
 *弧形进度条
 * Created by liubaba.
 */
public class PercentView extends View{
    private Paint paint;//画笔
    private Paint shaderPaint;//彩色画笔
    private Paint bitmapPaint;//图片画笔
    private Paint textPaint;//文字画笔
    /**控件宽度*/
    private int width;
    /**控件高度*/
    private int height;
    /**半径*/
    private int radius;
    /**外圆弧的宽度*/
    private float outerArcWidth;
    /**内部大圆弧的宽度*/
    private float insideArcWidth;
    /**两圆弧中间间隔距离*/
    private float spaceWidth;
    /**两圆弧中间间隔距离*/
    private float percentTextSize;
    /**最外层滑动小球的半径*/
    private float scrollCircleRadius;
    /**粉红底色*/
    private int pinkColor;
    /**黄色*/
    private int yellowColor;
    /**粉色红*/
    private int pinkRedColor;
    /**浅红*/
    private int redColor;
    /**深红*/
    private int deepRedColor;
    /**灰色*/
    private int grayColor;
    /**间隔的角度*/
    private double spaceAngle=22.5;
    /**两条圆弧的起始角度*/
    private double floatAngel=30;
    /**自定义的Bitmap*/
    private Bitmap mBitmap;
    /**自定义的画布，目的是为了能画出重叠的效果*/
    private Canvas mCanvas;
    /**时刻变化的Angel*/
    private double mAngel;
    /**内弧半径*/
    private float insideArcRadius;
    private double aimPercent=0;
    private float outerArcRadius;
    private float[] pos;                // 当前点的实际位置
    private float[] tan;                // 当前点的tangent值,用于计算图片所需旋转的角度
    private Bitmap mBitmapBackDeepRed;  // 箭头图片
    private Bitmap mBitmapBackYellow;  // 箭头图片
    private Bitmap mBitmapBackPink;  // 箭头图片
    private Bitmap mBitmapBackRed;  // 箭头图片
    private Matrix mMatrix;             // 矩阵,用于对图片进行一些操作
    private RectF outerArea;            //外圈的矩形
    private String tag;
    private String aim;
    private int textSizeTag;//名列前茅字体大小
    private int textSizeAim;//击败百分比字体大小

    private Bitmap mBitmapBack;
    // 动效过程监听器
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;
    //过程动画
    private ValueAnimator mValueAnimator;
    // 用于控制动画状态转换
    private Handler mAnimatorHandler;
    // 默认的动效周期 2s
    private int defaultDuration = 2000;


    public PercentView(Context context) {
        super(context);
        initView(context);

    }

    public PercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public PercentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }

    private void initView(Context context){
        shaderPaint=new Paint();
        textPaint=new Paint();

        paint=new Paint();
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);

        bitmapPaint=new Paint();
        bitmapPaint.setStyle(Paint.Style.FILL);
        bitmapPaint.setAntiAlias(true);

        outerArcWidth = context.getResources().getDimensionPixelOffset(R.dimen.dp2);
        insideArcWidth = context.getResources().getDimensionPixelOffset(R.dimen.dp12);
        spaceWidth = context.getResources().getDimensionPixelOffset(R.dimen.dp12);
        scrollCircleRadius = context.getResources().getDimensionPixelOffset(R.dimen.dp4);
        percentTextSize = context.getResources().getDimensionPixelOffset(R.dimen.dp8);
        textSizeAim = context.getResources().getDimensionPixelOffset(R.dimen.sp15);
        textSizeTag = context.getResources().getDimensionPixelOffset(R.dimen.sp30);
        pinkColor = context.getResources().getColor(R.color.percent_pink);
        yellowColor = context.getResources().getColor(R.color.percent_yellow);
        pinkRedColor = context.getResources().getColor(R.color.percent_yellow);
        redColor = context.getResources().getColor(R.color.percent_yellow);
        deepRedColor = context.getResources().getColor(R.color.percent_yellow);
        grayColor = context.getResources().getColor(R.color.percent_gray);


        pos = new float[2];
        tan = new float[2];
        mBitmapBackDeepRed= BitmapFactory.decodeResource(context.getResources(), R.mipmap.blur_back_deep_deep);
        mBitmapBackRed= BitmapFactory.decodeResource(context.getResources(), R.mipmap.blur_back_deep_red);
        mBitmapBackPink= BitmapFactory.decodeResource(context.getResources(), R.mipmap.blur_back_deep_pink);
        mBitmapBackYellow= BitmapFactory.decodeResource(context.getResources(), R.mipmap.blur_back_deep_yellow);
        mMatrix=new Matrix();


    }

    private int count=0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("PercentVIew", "开始绘制" + count);
        long startTime=System.currentTimeMillis();
        count++;
        width = getWidth(); //获取宽度
        height = getHeight();//获取高度
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas =new Canvas(mBitmap);

        radius= (int) (height/(1+Math.sin(Math.toRadians(spaceAngle))));//获取最外园的半径
        insideArcRadius= radius-scrollCircleRadius-spaceWidth;//内弧半径
//        Log.i(TAG,"最外园半径"+radius+"\n高度为"+height);
//        Log.i(TAG,"最外园半径"+Math.sin(Math.toRadians(spaceAngle)));
//        paintPercentText(mCanvas);
        paintPercentBack(mCanvas);
        paintPercent(mAngel, aimPercent, mCanvas);
//        calculateItemPositions(aimPercent,increaseValue,mCanvas,mBitmapBackDeepRed);
        //将Bitmap画到Canvas
//        paintText(mCanvas);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        long endTime=System.currentTimeMillis();
        Log.i("PercentVIew", "绘制结束" + (endTime-startTime));
    }

    /**
     * 旋转画布画刻度
     * @param canvas 画布
     */
    private void paintPercentText(Canvas canvas){
        paint.setTextSize(percentTextSize);
        paint.setColor(pinkColor);
        paint.setStrokeWidth(1);
        paint.setTextAlign(Paint.Align.CENTER);
        for (int i=0;i<=10;i++){
            //保存画布
            canvas.save();
            //旋转角度，第一个参数是旋转的角度、第二个参数和第三个参数是旋转中心点x和y
            canvas.rotate((float) (spaceAngle * i + -135 + spaceAngle), width / 2, radius);
            //画文字
            canvas.drawText(i * 10 + "", width / 2,  outerArcWidth + insideArcWidth + spaceWidth * 2, paint);
            canvas.restore();
        }
    }
    /**画两条线的底色*/
    private void paintPercentBack(Canvas canvas){
        paint.setColor(grayColor);
        paint.setStrokeWidth(outerArcWidth);//outerArcWidth
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);//设置为圆角
        paint.setAntiAlias(true);
        //绘制最外层圆条底色
        outerArcRadius=radius-outerArcWidth;
        outerArea= new RectF(width/2 - outerArcRadius, radius - outerArcRadius, width/2  + outerArcRadius, radius + outerArcRadius);
//        canvas.drawArc(outerArea,
//                (float) (180 - floatAngel),
//                (float) (180 + 2 * floatAngel), false, paint);

        //测试代码star
        float left=getWidth()/2-getHeight()/2;
        float top=7;
        float right=getWidth()/2+getHeight()/2;
        float bottom=getHeight()-7;
        RectF oval = new RectF( left, top,
                right, bottom);
        canvas.drawArc(oval,
                (float) (180 - floatAngel),
                (float) (180 + 2 * floatAngel), false, paint);
        //测试代码end





    }

    /***
     * 4个色值由浅到深分别是 ffd200 ff5656 fa4040 f60157
     * 绘制外层和内层的颜色线条
     * 主要用到Xfermode的SRC_ATOP显示上层绘制
     * setStrokeCap   Paint.Cap.ROUND设置为圆角矩形
     */
    private void paintPercent(double percent,double aimPercent,Canvas canvas){
        double roateAngel=percent*0.01*225;
        shaderPaint.setColor(yellowColor);
        shaderPaint.setStrokeCap(Paint.Cap.ROUND);
        shaderPaint.setAntiAlias(true);
        shaderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));//shaderPaint.setColor(yellowColor);
        if (aimPercent>=0&&aimPercent<=20){
//            int colorSweep[] = { yellowColor,yellowColor };
//            float position[]={0.2f,0.7f};
//            SweepGradient sweepGradient=new SweepGradient(width / 2, radius, colorSweep, position);
//            shaderPaint.setShader(sweepGradient);
        }else if (aimPercent>20&&aimPercent<=60){
            int colorSweep[] = { yellowColor,pinkRedColor };
            float position[]={0.5f,0.7f};
            SweepGradient sweepGradient=new SweepGradient(width / 2, radius, colorSweep, position);
            shaderPaint.setShader(sweepGradient);
        }else if (aimPercent>60&&aimPercent<=90){
            int colorSweep[] = {redColor, yellowColor,yellowColor,pinkRedColor,redColor };
            float position[]={0.25f,0.35f,0.5f,0.7f,0.8f};
            SweepGradient sweepGradient=new SweepGradient(width / 2, radius, colorSweep, position);
            shaderPaint.setShader(sweepGradient);
        }else if (aimPercent>90){
            int colorSweep[] = {deepRedColor, yellowColor,yellowColor,pinkRedColor,redColor, deepRedColor};
            float position[]={0.2f,0.4f,0.5f,0.7f,0.9f,1.0f};
            SweepGradient sweepGradient=new SweepGradient(width / 2, radius, colorSweep, position);
            shaderPaint.setShader(sweepGradient);
        }
        if (aimPercent<=10){//目的是为了
            drawOuterAcr((float) (180 - floatAngel), (float) roateAngel, canvas,mBitmapBack,yellowColor);
        }else if (aimPercent>10&&aimPercent<=20){
            drawOuterAcr((float) (180 - floatAngel), (float) roateAngel, canvas,mBitmapBack,yellowColor);
        }else if (aimPercent>20&&aimPercent<=60){
            drawOuterAcr((float) (180 - floatAngel), (float) (roateAngel - (spaceAngle - floatAngel)), canvas,mBitmapBack,pinkRedColor);
        }else if (aimPercent>60&&aimPercent<=90){
            drawOuterAcr((float) (180 - floatAngel), (float) (roateAngel - (spaceAngle - floatAngel)),canvas,mBitmapBack,redColor);
        }else {
            drawOuterAcr((float) (180 - floatAngel), (float) (roateAngel-2*(spaceAngle-floatAngel)), canvas,mBitmapBack, deepRedColor);
        }


    }

    /***
     * 画内部圆环渐变
     * @param formDegree 起始角度
     * @param toDegree 旋转角度
     * @param canvas 画布
     */
    private void drawInsideArc(float formDegree ,float toDegree,Canvas canvas){
//        shaderPaint.setStrokeWidth(insideArcWidth);
//        shaderPaint.setStyle(Paint.Style.STROKE);
//        //内弧半径
//        float left=getWidth()/2-getHeight()/2;
//        float top=0;
//        float right=getWidth()/2+getHeight()/2;
//        float bottom=getHeight();
//        RectF oval = new RectF( left, top,
//                right, bottom);
//        canvas.drawArc(oval,
//                formDegree,
//                toDegree, false, shaderPaint);

    }

    /***
     * 绘制外部彩色线条和小红圈
     * 利用PathMeasure的getTranslate测量出需要绘制的圆弧的末端的坐标位置
     * @param formDegree 起始角度
     * @param toDegree 旋转角度
     * @param canvas 画布
     * @param bitmap 四种状态的模糊Bitmap
     * @param color 四种状态的实心颜色
     */
    private void drawOuterAcr(float formDegree ,float toDegree,Canvas canvas,Bitmap bitmap,int color){
        shaderPaint.setStrokeWidth(outerArcWidth);
        shaderPaint.setStyle(Paint.Style.STROKE);
//        canvas.drawArc( new RectF(width/2 - outerArcRadius, radius - outerArcRadius, width/2  + outerArcRadius, radius + outerArcRadius),
//                formDegree,
//                toDegree, false, shaderPaint);
        if (toDegree!=0) {
            Path orbit = new Path();
            //通过Path类画一个90度（180—270）的内切圆弧路径
//            orbit.addArc(outerArea, formDegree, toDegree);

            //测试代码star
            float left=getWidth()/2-getHeight()/2;
            float top=7;
            float right=getWidth()/2+getHeight()/2;
            float bottom=getHeight()-7;
            RectF oval = new RectF( left, top,
                    right, bottom);
            orbit.addArc(oval, formDegree, toDegree);
            //测试代码end

            // 创建 PathMeasure
            PathMeasure measure = new PathMeasure(orbit, false);
            measure.getPosTan(measure.getLength() * 1, pos, tan);
            mMatrix.reset();
            mMatrix.postTranslate(pos[0] - bitmap.getWidth() / 2, pos[1] - bitmap.getHeight() / 2);   // 将图片绘制中心调整到与当前点重合
            canvas.drawPath(orbit, shaderPaint);//绘制外层的线条
            canvas.drawBitmap(bitmap, mMatrix, bitmapPaint);//绘制
//            bitmapPaint.setColor(color);
            //绘制实心小圆圈
            canvas.drawCircle(pos[0], pos[1], 8, bitmapPaint);
        }
    }
    /***
     * 4个色值由浅到深分别是 ffd200 ff5656 fa4040 f60157
     * 等级划分：0-20% 再接再厉   21-60% 技高一筹   61-90% 名列前茅   90以上 理财达人
     */
    private void paintText(Canvas canvas){
        if (!TextUtils.isEmpty(tag)&&!TextUtils.isEmpty(aim)){
            if (aimPercent>=0&&aimPercent<=20){
                textPaint.setColor(yellowColor);
            }else if (aimPercent>20&&aimPercent<=60){
                textPaint.setColor(pinkRedColor);
            }else if (aimPercent>60&&aimPercent<=90){
                textPaint.setColor(redColor);
            }else {
                textPaint.setColor(deepRedColor);
            }
            textPaint.setTextSize(textSizeTag);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setStrokeWidth(2);
            canvas.drawText(tag, width / 2, radius - textSizeTag / 2, textPaint);
            textPaint.setColor(grayColor);
            textPaint.setTextSize(textSizeAim);
            textPaint.setStrokeWidth(1);
            float leftLength=textPaint.measureText("你击败了");
            float rightLength=textPaint.measureText("的用户");
            float centerLength=textPaint.measureText(aim+"%");
            float rightOffest=textSizeAim/2;//
            canvas.drawText("你击败了",width/2-leftLength/2-centerLength/2+rightOffest,radius + textSizeAim, textPaint);
            canvas.drawText("的用户",width/2+rightLength/2+centerLength/2+rightOffest,radius + textSizeAim, textPaint);
            textPaint.setColor(Color.parseColor("#fa4040"));
            canvas.drawText(aim+"%",width/2+rightOffest,radius + textSizeAim, textPaint);

        }


    }

    /**
     * 设置角度变化，刷新界面
     * @param aimPercent 目标百分比
     */
    public void setAngel(double aimPercent){
        //两边监测
        if (aimPercent<1){
            aimPercent=1;
        }else if (aimPercent>99){
            aimPercent=100;
        }
        this.aimPercent=aimPercent;
        initListener();

        initHandler();

        initAnimator();
        mValueAnimator.start();

    }

    /**
     * 设置文字
     * @param tag 名列前茅文案
     * @param aim 击败的百分比
     */
    public void setRankText(String tag,String aim){
        this.tag=tag;
        this.aim=aim;
        mAnimatorHandler.sendEmptyMessage(1);

    }

    private void initListener() {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAngel = (float) animation.getAnimatedValue()*aimPercent;
                if (mAngel>=0&&mAngel<=20){
                    bitmapPaint.setColor(yellowColor);
                    mBitmapBack=mBitmapBackYellow;
                }else if (mAngel>20&&mAngel<=60){
                    bitmapPaint.setColor(pinkRedColor);
                    mBitmapBack=mBitmapBackPink;
                }else if (mAngel>60&&mAngel<=90){
                    bitmapPaint.setColor(redColor);
                    mBitmapBack=mBitmapBackRed;
                }else {
                    bitmapPaint.setColor(deepRedColor);
                    mBitmapBack=mBitmapBackDeepRed;
                }
//                Log.i("TAG", "mAnimatorValue="+mAnimatorValue);
                invalidate();
            }
        };

        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // getHandle发消息通知动画状态更新
                mAnimatorHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    private void initHandler() {
        mAnimatorHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        mValueAnimator.removeAllUpdateListeners();
                        mValueAnimator.removeAllListeners();
                        break;
                    case 1:
                        invalidate();
                        break;
                }

            }
        };
    }

    private void initAnimator() {
        mValueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);

        mValueAnimator.addUpdateListener(mUpdateListener);

        mValueAnimator.addListener(mAnimatorListener);
    }


}
