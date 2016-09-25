package GUI;

import android.app.Activity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;

import Mediator.StitchMediator;
import dennymades.space.StitchAndroid.MyGLSurfaceView;
import dennymades.space.StitchAndroid.R;

/**
 * Created by abrain on 9/22/16.
 */
public class RootLayout implements View.OnDragListener{
    private String TAG = this.getClass().getName();
    private FrameLayout rootLayout;
    private MyGLSurfaceView temp;
    private StitchMediator stitchMediator;
    private int resourceId;

    public RootLayout(int rId, StitchMediator sm){
        stitchMediator = sm;
        resourceId = rId;
        setResourceId();
        temp = (MyGLSurfaceView)sm.getActivity().findViewById(R.id.renderer_view);

        temp.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {
                // TODO Auto-generated method stub
                final int action = event.getAction();
                switch(action) {

                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.d(TAG, "drag started");
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.d(TAG, "drag exited");
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d(TAG, "drag entered");
                        break;

                    case DragEvent.ACTION_DROP:{
                        Log.d(TAG, "drag dropped");
                        return(true);
                    }

                    case DragEvent.ACTION_DRAG_ENDED:{
                        Log.d(TAG, "drag ended");
                        return(true);

                    }

                    default:
                        break;
                }
                return true;
            }});

        rootLayout.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {
                // TODO Auto-generated method stub
                final int action = event.getAction();
                switch(action) {

                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.d(TAG, "drag started");
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.d(TAG, "drag exited");
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d(TAG, "drag entered");
                        break;

                    case DragEvent.ACTION_DROP:{
                        Log.d(TAG, "drag dropped");
                        return(true);
                    }

                    case DragEvent.ACTION_DRAG_ENDED:{
                        Log.d(TAG, "drag ended");
                        return(true);

                    }

                    default:
                        break;
                }
                return true;
            }});
    }

    private void setResourceId(){
        rootLayout = (FrameLayout) stitchMediator.getActivity().findViewById(resourceId);
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        Log.d(TAG, "drag");
        return false;
    }
}
