package ai.leqi.lib_share_center.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import ai.leqi.lib_share_center.R;
import ai.leqi.lib_share_center.helper.ShareCenterHelper;

public class WechatManager {
    private IWXAPI mWxApi;
    private final Context mContext;
    private static volatile WechatManager mInstance;

    private WechatManager(Context context) {
        this.mContext = context;
        //初始化数据
        //初始化微信分享代码
        initWechatShare(context);
    }

    public static WechatManager getInstance(Context context) {
        //避免不必要的同步
        if (mInstance == null) {
            synchronized (WechatManager.class) {
                if (mInstance == null) {
                    mInstance = new WechatManager(context);
                }
            }
        }
        return mInstance;
    }

    private void initWechatShare(Context context) {
        if (mWxApi == null) {
            mWxApi = WXAPIFactory.createWXAPI(context, ShareCenterHelper.WECHAT_APP_ID, true);
        }
        mWxApi.registerApp(ShareCenterHelper.WECHAT_APP_ID);
    }

    //分享文字
    public void shareText(String text, String description, int scene) {
        if (mWxApi.isWXAppInstalled()) {
            if (mWxApi.getWXAppSupportAPI() < Build.TIMELINE_SUPPORTED_SDK_INT) {
                Toast.makeText(mContext, R.string.wechat_share_not_support, Toast.LENGTH_SHORT).show();
                return;
            }
            //初始化一个 WXTextObject 对象，填写分享的文本内容
            WXTextObject textObj = new WXTextObject();
            textObj.text = text;

            //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = textObj;
            msg.description = description;

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("text");
            req.message = msg;
            req.scene = scene;
            //调用api接口，发送数据到微信
            mWxApi.sendReq(req);
        } else {
            Toast.makeText(mContext, R.string.wechat_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    //分享图片
    public void shareImg(Bitmap bitmap, int scene) {
        if (mWxApi.isWXAppInstalled()) {
            if (mWxApi.getWXAppSupportAPI() < Build.TIMELINE_SUPPORTED_SDK_INT) {
                Toast.makeText(mContext, R.string.wechat_share_not_support, Toast.LENGTH_SHORT).show();
                return;
            }
            WXImageObject imgObj = new WXImageObject(bitmap);

            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObj;

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("img");
            req.message = msg;
            req.scene = scene;
            mWxApi.sendReq(req);
        } else {
            Toast.makeText(mContext, R.string.wechat_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    //获取微信code
    public void getWechatCode() {
        if (mWxApi.isWXAppInstalled()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "share_center_sdk_android";
            mWxApi.sendReq(req);
        } else {
            Toast.makeText(mContext, R.string.wechat_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    private String buildTransaction(String type) {
        if (TextUtils.isEmpty(type)) {
            return String.valueOf(System.currentTimeMillis());
        } else {
            return type + System.currentTimeMillis();
        }
    }
}
