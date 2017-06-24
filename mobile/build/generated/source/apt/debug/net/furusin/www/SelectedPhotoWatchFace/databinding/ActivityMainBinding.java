package net.furusin.www.SelectedPhotoWatchFace.databinding;
import net.furusin.www.SelectedPhotoWatchFace.R;
import net.furusin.www.SelectedPhotoWatchFace.BR;
import android.view.View;
public class ActivityMainBinding extends android.databinding.ViewDataBinding implements android.databinding.generated.callback.OnClickListener.Listener {

    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.adView, 2);
        sViewsWithIds.put(R.id.imageView, 3);
    }
    // views
    public final com.google.android.gms.ads.AdView adView;
    public final android.widget.Button button;
    public final android.widget.ImageView imageView;
    private final android.widget.RelativeLayout mboundView0;
    // variables
    private net.furusin.www.SelectedPhotoWatchFace.viewInterface.MainViewInterface mMainViewInterface;
    private final android.view.View.OnClickListener mCallback1;
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityMainBinding(android.databinding.DataBindingComponent bindingComponent, View root) {
        super(bindingComponent, root, 0);
        final Object[] bindings = mapBindings(bindingComponent, root, 4, sIncludes, sViewsWithIds);
        this.adView = (com.google.android.gms.ads.AdView) bindings[2];
        this.button = (android.widget.Button) bindings[1];
        this.button.setTag(null);
        this.imageView = (android.widget.ImageView) bindings[3];
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        mCallback1 = new android.databinding.generated.callback.OnClickListener(this, 1);
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean setVariable(int variableId, Object variable) {
        switch(variableId) {
            case BR.mainViewInterface :
                setMainViewInterface((net.furusin.www.SelectedPhotoWatchFace.viewInterface.MainViewInterface) variable);
                return true;
        }
        return false;
    }

    public void setMainViewInterface(net.furusin.www.SelectedPhotoWatchFace.viewInterface.MainViewInterface MainViewInterface) {
        this.mMainViewInterface = MainViewInterface;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.mainViewInterface);
        super.requestRebind();
    }
    public net.furusin.www.SelectedPhotoWatchFace.viewInterface.MainViewInterface getMainViewInterface() {
        return mMainViewInterface;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        net.furusin.www.SelectedPhotoWatchFace.viewInterface.MainViewInterface mainViewInterface = mMainViewInterface;
        // batch finished
        if ((dirtyFlags & 0x2L) != 0) {
            // api target 1

            this.button.setOnClickListener(mCallback1);
        }
    }
    // Listener Stub Implementations
    // callback impls
    public final void _internalCallbackOnClick(int sourceId , android.view.View callbackArg_0) {
        // localize variables for thread safety
        // mainViewInterface
        net.furusin.www.SelectedPhotoWatchFace.viewInterface.MainViewInterface mainViewInterface = mMainViewInterface;
        // mainViewInterface != null
        boolean mainViewInterfaceJavaLangObjectNull = false;



        mainViewInterfaceJavaLangObjectNull = (mainViewInterface) != (null);
        if (mainViewInterfaceJavaLangObjectNull) {


            mainViewInterface.selectPhoto();
        }
    }
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;

    public static ActivityMainBinding inflate(android.view.LayoutInflater inflater, android.view.ViewGroup root, boolean attachToRoot) {
        return inflate(inflater, root, attachToRoot, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static ActivityMainBinding inflate(android.view.LayoutInflater inflater, android.view.ViewGroup root, boolean attachToRoot, android.databinding.DataBindingComponent bindingComponent) {
        return android.databinding.DataBindingUtil.<ActivityMainBinding>inflate(inflater, net.furusin.www.SelectedPhotoWatchFace.R.layout.activity_main, root, attachToRoot, bindingComponent);
    }
    public static ActivityMainBinding inflate(android.view.LayoutInflater inflater) {
        return inflate(inflater, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static ActivityMainBinding inflate(android.view.LayoutInflater inflater, android.databinding.DataBindingComponent bindingComponent) {
        return bind(inflater.inflate(net.furusin.www.SelectedPhotoWatchFace.R.layout.activity_main, null, false), bindingComponent);
    }
    public static ActivityMainBinding bind(android.view.View view) {
        return bind(view, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static ActivityMainBinding bind(android.view.View view, android.databinding.DataBindingComponent bindingComponent) {
        if (!"layout/activity_main_0".equals(view.getTag())) {
            throw new RuntimeException("view tag isn't correct on view:" + view.getTag());
        }
        return new ActivityMainBinding(bindingComponent, view);
    }
    /* flag mapping
        flag 0 (0x1L): mainViewInterface
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}