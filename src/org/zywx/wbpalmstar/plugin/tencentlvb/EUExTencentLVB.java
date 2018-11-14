package org.zywx.wbpalmstar.plugin.tencentlvb;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.DataHelper;
import org.zywx.wbpalmstar.engine.EBrowserActivity;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.plugin.tencentlvb.util.MainActivity;
import org.zywx.wbpalmstar.plugin.tencentlvb.vo.DataVO;

public class EUExTencentLVB extends EUExBase {

	public static final int ACTION_PUBLISH = 1;
	public static final int ACTION_PLAY_VOD = 2;
	public static final int ACTION_PLAY_LIVE = 3;
	public static final String TEXT_ACTION = "action";
	public static final String TEXT_URL = "url";
	public static final String TEXT_IMAGE = "image";
    public static final String BOOLEAN_IS_SHOW_LOG = "isShowLog";

    private String[] vodPlayParams;
    private String[] publishParams;
    private String[] livePlayParams;

    public EUExTencentLVB(Context context, EBrowserView eBrowserView) {
		super(context, eBrowserView);
	}


    public void vodPlay(String [] params) {
        vodPlayParams = params;
        // android6.0以上动态权限申请
        if (mContext.checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            requsetPerssions(Manifest.permission.RECORD_AUDIO, "请先申请权限"
                    + Manifest.permission.RECORD_AUDIO, 1);
        } else {
            if (params.length != 1) {
                return;
            }
            DataVO dataVO = DataHelper.gson.fromJson(params[0], DataVO.class);
            actionHandler(dataVO, ACTION_PLAY_VOD);
        }
    }
    public void livePlay(String [] params) {
        livePlayParams = params;
        // android6.0以上动态权限申请
        if (mContext.checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            requsetPerssions(Manifest.permission.RECORD_AUDIO, "请先申请权限"
                    + Manifest.permission.RECORD_AUDIO, 2);
        } else {
            if (params.length != 1) {
                return;
            }
            DataVO dataVO = DataHelper.gson.fromJson(params[0], DataVO.class);
            actionHandler(dataVO, ACTION_PLAY_LIVE);
        }

    }
    public void publish(String [] params) {
        publishParams = params;
        // android6.0以上动态权限申请
        if (mContext.checkCallingOrSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            requsetPerssions(Manifest.permission.CAMERA, "请先申请权限"
                    + Manifest.permission.CAMERA, 3);
        } else {
            if (params.length != 1) {
                return;
            }
            DataVO dataVO = DataHelper.gson.fromJson(params[0], DataVO.class);
            actionHandler(dataVO, ACTION_PUBLISH);
        }
    }
    public void actionHandler(String [] params, int action) {
        try {
            JSONObject json = new JSONObject(params[0]);
            String url = json.optString("url", null);
            String imageUrl = json.optString("bgImage", null);
            if (!TextUtils.isEmpty(url)) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(TEXT_ACTION, action);
                intent.putExtra(TEXT_URL, url);
                String image = BUtility.makeRealPath(
                        BUtility.makeUrl(mBrwView.getCurrentUrl(), imageUrl),
                        mBrwView.getCurrentWidget().m_widgetPath,
                        mBrwView.getCurrentWidget().m_wgtType);
                intent.putExtra(TEXT_IMAGE, image);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void actionHandler(DataVO data, int action) {
        String url = data.getUrl();
        String imageUrl = data.getBgImage();
        boolean isShowLog = data.getOptions().isShowLog();
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra(TEXT_ACTION, action);
            intent.putExtra(TEXT_URL, url);
            String image = BUtility.makeRealPath(
                    BUtility.makeUrl(mBrwView.getCurrentUrl(), imageUrl),
                    mBrwView.getCurrentWidget().m_widgetPath,
                    mBrwView.getCurrentWidget().m_wgtType);
            intent.putExtra(TEXT_IMAGE, image);
            intent.putExtra(BOOLEAN_IS_SHOW_LOG, isShowLog);
            startActivity(intent);
        }

    }
    @Override
	protected boolean clean() {
		return true;
	}

    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] != PackageManager.PERMISSION_DENIED) {
                vodPlay(vodPlayParams);
            } else {
                // 对于 ActivityCompat.shouldShowRequestPermissionRationale
                // 1：用户拒绝了该权限，没有勾选"不再提醒"，此方法将返回true。
                // 2：用户拒绝了该权限，有勾选"不再提醒"，此方法将返回 false。
                // 3：如果用户同意了权限，此方法返回false
                // 拒绝了权限且勾选了"不再提醒"
                if (!ActivityCompat.shouldShowRequestPermissionRationale((EBrowserActivity) mContext, permissions[0])) {
                    Toast.makeText(mContext, "请先设置权限" + permissions[0], Toast.LENGTH_LONG).show();
                } else {
                    requsetPerssions(Manifest.permission.RECORD_AUDIO, "请先申请权限" + permissions[0], 1);
                }
            }
        } else if (requestCode == 2){
            if (grantResults[0] != PackageManager.PERMISSION_DENIED){
                livePlay(livePlayParams);
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((EBrowserActivity)mContext, permissions[0])) {
                    Toast.makeText(mContext, "请先设置权限" + permissions[0], Toast.LENGTH_LONG).show();
                } else {
                    requsetPerssions(Manifest.permission.RECORD_AUDIO, "请先申请权限" + permissions[0], 1);
                }
            }
        }else if (requestCode == 3){
            if (grantResults[0] != PackageManager.PERMISSION_DENIED){
                publish(publishParams);
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((EBrowserActivity)mContext, permissions[0])) {
                    Toast.makeText(mContext, "请先设置权限" + permissions[0], Toast.LENGTH_LONG).show();
                } else {
                    requsetPerssions(Manifest.permission.CAMERA, "请先申请权限" + permissions[0], 1);
                }
            }
        }
    }
}
