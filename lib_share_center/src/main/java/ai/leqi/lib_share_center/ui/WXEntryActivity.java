package ai.leqi.lib_share_center.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import ai.leqi.lib_share_center.R;
import ai.leqi.lib_share_center.event.WXCodeEvent;
import ai.leqi.lib_share_center.helper.ShareCenterHelper;


public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI mWxApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWxApi = WXAPIFactory.createWXAPI(this, ShareCenterHelper.WECHAT_APP_ID, true);
        try {
            Intent intent = getIntent();
            mWxApi.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWxApi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            SendAuth.Resp authResp = (SendAuth.Resp) resp;
            final String code = authResp.code;
            EventBus.getDefault().post(new WXCodeEvent(code));
        } else {
            int result;

            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    result = R.string.errcode_success;
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    result = R.string.errcode_cancel;
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    result = R.string.errcode_deny;
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    result = R.string.errcode_unsupported;
                    break;
                default:
                    result = R.string.errcode_unknown;
                    break;
            }

            Toast.makeText(this, getString(result) /*+ ", type=" + resp.getType()*/, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

}