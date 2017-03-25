//JavaClock.java
package ui;
import java.applet.Applet;
import java.awt.*;
import java.util.Date;

public class JavaClock extends Applet
                       implements Runnable
{
//*************************定义变量*****************************
    static final Object colors[][];
    static final Object cityZone[][] = {
        {
            "Hawaii", "-10"
        }, {
            "Alaska", "-9"
        }, {
            "Arizona", "-7"
        }, {
            "Mexico", "-6"
        }, {
            "Brasilia", "-3"
        }, {
            "GMT", "0"
        }, {
            "Paris", "1"
        }, {
            "Cairo", "2"
        }, {
            "Moscow", "3"
        }, {
            "Jakarta", "7"
        }, {
            "Hong Kong", "8"
        }, {
            "Tokyo", "9"
        }, {
            "Sydney", "10"
        }
    };
    Thread threadClock;
    boolean isAnalog;
    Font fontText;
    Color fontColor;
    Color backColor;
    Color hHandColor;
    Color mHandColor;
    Color sHandColor;
    Color hPointColor;
    Color mPointColor;
    int xPoint[];
    int yPoint[];
    Image backImage;
    MediaTracker tracker;
    Image imageBuffer;
    int fromGMT;
    int currentZone;
    int oldHour;
    int oldMinute;
    int oldSecond;
    
//*****************************************************************
    public void setCityZone(String s)
    {//由得到的地区参数设置currentZone的值
        currentZone = fromGMT;
        for(int i = 0; i < cityZone.length; i++)
        {
            if(s.indexOf((String)cityZone[i][0]) != -1)
            {
                currentZone = Integer.parseInt((String)cityZone[i][1]);
            }
        }

    }

//*****************************************************************
    private Color findColor(String s, Color color)
    {//用同一函数处理从html文件得到的各个部分的颜色
     //包括前景色,背景色,时分秒的指针颜色,表盘上的时刻指示点
        if(s != null)
        {
            s = s.toUpperCase();
            if(s.charAt(0) == '#')
            {
                return new Color(Integer.parseInt(s.substring(1), 16));
            }
            for(int i = 0; i < colors.length; i++)
            {
                if(s.compareTo((String)colors[i][0]) == 0)
                {
                    return (Color)colors[i][1];
                }
            }

        }
        return color;
    }
    
//*****************************************************************
    public void init()
    {//得到并处理参数,分别设置字体,大小,各个部分的颜色
     //包括"前景色,背景色,时分秒的指针颜色,表盘上的时刻指示点"
     //并初始化日期时间,isAnalog等值 
        String s = getParameter("typeface");
        if(s == null)
        {
            s = "Helvetica";
        }
        
        int i;
        try
        {
            i = Integer.parseInt(getParameter("fontsize"), 10);
        }
        catch(NumberFormatException _ex)
        {
            i = 16;
        }
        fontText = new Font(s, 0, i);
        
        backColor = findColor(getParameter("backcolor"), backColor);
        fontColor = findColor(getParameter("fontcolor"), fontColor);
        hHandColor = findColor(getParameter("hhandcolor"), hHandColor);
        mHandColor = findColor(getParameter("mhandcolor"), mHandColor);
        sHandColor = findColor(getParameter("shandcolor"), sHandColor);
        hPointColor = findColor(getParameter("hpointcolor"), hPointColor);
        mPointColor = findColor(getParameter("mpointcolor"), mPointColor);
        
        tracker = new MediaTracker(this);
        
        s = getParameter("backimage");
        if(s != null)
        {
            backImage = getImage(getCodeBase(), s);
            tracker.addImage(backImage, 0);
        } else
        {
            backImage = null;
        }
        
        s = getParameter("analog");
        if(s != null && s.indexOf("false") > -1)
        {
            isAnalog = false;
        } else
        {
            isAnalog = true;
        }
        
        Date date = new Date();
        fromGMT = -Math.round((float)date.getTimezoneOffset() / 60F);
        currentZone = fromGMT;
    }


//********************************************************************* 
    public void start()
    {
        if(imageBuffer == null)//画背景图
        {
            Dimension dimension = size();
            imageBuffer = createImage(dimension.width, dimension.height);
        }
        if(threadClock == null)//开始线程
        {
            threadClock = new Thread(this);
            threadClock.start();
        }
    }
    
    
//**********************************************************************
    public void stop()
    {//停止
        if(threadClock != null)
        {
            threadClock.stop();
            threadClock = null;
            imageBuffer = null;
        }
    }


//**********************************************************************
    public void run()
    {//运行线程,如果当前线程是threadClock,repaint,等待50L的时间,返回
        try
        {
            tracker.waitForAll();
        }
        catch(InterruptedException _ex)
        {
            return;
        }
        while(Thread.currentThread() == threadClock) 
        {
            repaint();
            try
            {
                Thread.sleep(50L);
            }
            catch(InterruptedException _ex)
            {
                return;
            }
        }
    }


//**********************************************************************
    public void update(Graphics g)
    {//更新画面
        Date date = new Date();
        
        int i = (date.getHours() + currentZone) - fromGMT;//以24h制表示的时
        if(i >= 24)
        {
            i -= 24;
        }
        if(i < 0)
        {
            i += 24;
        }
        
        int j = date.getMinutes();//分
        int k = date.getSeconds();//秒
        
        if(i != oldHour || j != oldMinute || k != oldSecond)
        {//如果当前时分秒的值和前记录值不同,则重新画时钟界面
            Graphics g1 = imageBuffer.getGraphics();
            DrawBackground(g1);
            Dimension dimension = size();
            int l = dimension.width >> 1;
            int i1 = dimension.height >> 1;
            
            if(isAnalog)
            {//机械表的界面
            
              //**********************分针************************
                double d = (double)Math.min(l, i1) * 0.75D;//定出分针的各个顶点
                double d1 = (double)Math.min(l, i1) * 0.040000000000000001D;
                double d2 = 3.1415926535897931D * ((double)j / 30D + (double)k / 1800D);
                xPoint[0] = (int)Math.round((double)l - 2D * d1 * Math.sin(d2)) - 1;
                xPoint[1] = (int)Math.round((double)l - d1 * Math.cos(d2));
                xPoint[2] = (int)Math.round((double)l + d * Math.sin(d2)) + 1;
                xPoint[3] = (int)Math.round((double)l + d1 * Math.cos(d2));
                yPoint[0] = (int)Math.round((double)i1 + 2D * d1 * Math.cos(d2)) - 1;
                yPoint[1] = (int)Math.round((double)i1 - d1 * Math.sin(d2));
                yPoint[2] = (int)Math.round((double)i1 - d * Math.cos(d2)) + 1;
                yPoint[3] = (int)Math.round((double)i1 + d1 * Math.sin(d2));
                                
                g1.setColor(mHandColor);//分针颜色
                g1.fillPolygon(xPoint, yPoint, 4);//以多半形的形式画出分针(常见的拉长菱形)
               
                if(j < 30)//下面的是根据分针在左边还是右边来画出边线,使有光照的效果
                {
                    g1.setColor(Color.white);
                } else
                {
                    g1.setColor(Color.black);
                }
                g1.drawLine(xPoint[0], yPoint[0], xPoint[1], yPoint[1]);
                g1.drawLine(xPoint[1], yPoint[1], xPoint[2], yPoint[2]);
                if(j < 30)
                {
                    g1.setColor(Color.black);
                } else
                {
                    g1.setColor(Color.white);
                }
                g1.drawLine(xPoint[2], yPoint[2], xPoint[3], yPoint[3]);
                g1.drawLine(xPoint[3], yPoint[3], xPoint[0], yPoint[0]);
                
                
              //*********************时针**********************
                //时针的各个顶点
                double d3 = 3.1415926535897931D * ((double)i / 6D + (double)j / 360D);
                d = (double)Math.min(l, i1) * 0.5D;
                d1 = (double)Math.min(l, i1) * 0.050000000000000003D;
                xPoint[0] = (int)Math.round((double)l - 2D * d1 * Math.sin(d3)) - 1;
                xPoint[1] = (int)Math.round((double)l - d1 * Math.cos(d3));
                xPoint[2] = (int)Math.round((double)l + d * Math.sin(d3)) + 1;
                xPoint[3] = (int)Math.round((double)l + d1 * Math.cos(d3));
                yPoint[0] = (int)Math.round((double)i1 + 2D * d1 * Math.cos(d3)) - 1;
                yPoint[1] = (int)Math.round((double)i1 - d1 * Math.sin(d3));
                yPoint[2] = (int)Math.round((double)i1 - d * Math.cos(d3)) + 1;
                yPoint[3] = (int)Math.round((double)i1 + d1 * Math.sin(d3));
                
                g1.setColor(hHandColor);//时针颜色
                g1.fillPolygon(xPoint, yPoint, 4);//画多边形的形式作出时针
               
                if(i >= 0 && i <= 6 || i >= 12 && i <= 18)//画出光照效果的边线
                {
                    g1.setColor(Color.white);
                } else
                {
                    g1.setColor(Color.black);
                }
                g1.drawLine(xPoint[0], yPoint[0], xPoint[1], yPoint[1]);
                g1.drawLine(xPoint[1], yPoint[1], xPoint[2], yPoint[2]);
                if(i >= 0 && i <= 6 || i >= 12 && i <= 18)
                {
                    g1.setColor(Color.black);
                } else
                {
                    g1.setColor(Color.white);
                }
                g1.drawLine(xPoint[2], yPoint[2], xPoint[3], yPoint[3]);
                g1.drawLine(xPoint[3], yPoint[3], xPoint[0], yPoint[0]);
                
                //********************秒针*********************
                d = (double)Math.min(l, i1) * 0.75D;
                g1.setColor(sHandColor);
                double d4 = (3.1415926535897931D * (double)k) / 30D;
                g1.drawLine(l, i1, (int)Math.round((double)l + d * Math.sin(d4)), (int)Math.round((double)i1 - d * Math.cos(d4)));
            }
            
            
            
             else
            {//电子表界面
                g1.setFont(fontText);
                g1.setColor(fontColor);
                String s = (i >= 10 ? Integer.toString(i) : "0" + i) 
                         + ":" + (j >= 10 ? Integer.toString(j) : "0" + j) + ":" 
                         + (k >= 10 ? Integer.toString(k) : "0" + k);
                FontMetrics fontmetrics = g1.getFontMetrics();
               
                int j1 = dimension.height >> 1;
                if(j1 < 0)
                {
                    j1 = 0;
                }
                int k1 = dimension.width - fontmetrics.stringWidth(s) >> 1;
                if(k1 < 0)
                {
                    k1 = 0;
                }
                g1.drawString(s, k1, j1);//以字符串的形式画出电子表的界面
            }
            
            oldHour = i;//将当前的时分秒值记录下来,为下一步的比较做准备
            oldMinute = j;
            oldSecond = k;
        }
        
        g.drawImage(imageBuffer, 0, 0, null);//更新背景
    }


//**********************************paint函数***********************************
    public void paint(Graphics g)
    {  
        Dimension dimension = size();
        if(tracker.isErrorAny())
        {
            g.setColor(Color.white);
            g.fillRect(0, 0, dimension.width, dimension.height);
            return;
        }
        if(tracker.checkAll(true))
        {
            DrawBackground(g);
        }
    }


//*******************************MouseDown时间******************************
    //更改isAnalog的值,就是机械表和电子表画面的转换标志 
    public boolean mouseDown(Event event, int i, int j)
    {
        isAnalog = !isAnalog;
        return true;
    }


//**************************DrawBackground函数*******************************  
    private void DrawBackground(Graphics g)
    {
        Dimension dimension = size();
        g.setColor(backColor);
        g.fillRect(0, 0, dimension.width, dimension.height);
        if(backImage != null)
        {//有背景图则画出背景图
            int i = backImage.getWidth(this);
            int k = backImage.getHeight(this);
            if(i < 0 || k < 0)
            {
                return;
            }
            g.drawImage(backImage, dimension.width - i >> 1, dimension.height - k >> 1, null);
        }
        if(isAnalog)
        {//如果isAnalog为真,即是电子表界面,则画出电子表界面:表示"时分"的点
            int j = dimension.width >> 1;
            int l = dimension.height >> 1;
            double d = (double)Math.min(j, l) * 0.90000000000000002D;
            for(int i1 = 1; i1 <= 12; i1++)
            {//"时"刻度点
                double d1 = 3.1415926535897931D * (0.5D - (double)i1 / 6D);
                int k1 = (int)Math.floor((double)j + d * Math.cos(d1));
                int l1 = (int)Math.floor((double)l - d * Math.sin(d1));
                g.setColor(hPointColor);
                g.fill3DRect(k1 - 2, l1 - 2, 4, 4, true);
            }

            for(int j1 = 1; j1 <= 60; j1++)
            {//"分"刻度点
                if(j1 % 5 != 0)
                {
                    double d2 = (3.1415926535897931D * (double)j1) / 30D;
                    int i2 = (int)Math.floor((double)j + d * Math.cos(d2));
                    int j2 = (int)Math.floor((double)l - d * Math.sin(d2));
                    g.setColor(mPointColor);
                    g.fill3DRect(i2 - 2, j2 - 2, 3, 3, false);
                }
            }

        }
    }

   public JavaClock()
    {//默认值
        isAnalog = true;
        fontColor = Color.black;
        backColor = Color.lightGray;
        hHandColor = Color.blue;
        mHandColor = Color.blue;
        sHandColor = Color.black;
        hPointColor = Color.red;
        mPointColor = Color.lightGray;
        xPoint = new int[4];
        yPoint = new int[4];
        oldHour = -1;
        oldMinute = -1;
        oldSecond = -1;
    }

    static 
    {//构造颜色的函数
        colors = (new Object[][] {
            new Object[] {
                "BLACK", Color.black
            }, new Object[] {
                "BLUE", Color.blue
            }, new Object[] {
                "CYAN", Color.cyan
            }, new Object[] {
                "DARKGRAY", Color.darkGray
            }, new Object[] {
                "GRAY", Color.gray
            }, new Object[] {
                "GREEN", Color.green
            }, new Object[] {
                "LIGHTGRAY", Color.lightGray
            }, new Object[] {
                "MAGENTA", Color.magenta
            }, new Object[] {
                "ORANGE", Color.orange
            }, new Object[] {
                "PINK", Color.pink
            }, new Object[] {
                "RED", Color.red
            }, new Object[] {
                "WHITE", Color.white
            }, new Object[] {
                "YELLOW", Color.yellow
            }
        });
    }
}
