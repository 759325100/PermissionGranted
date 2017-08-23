# PermissionGranted
android6.0以上权限处理
众所周知，从android sdk 23开始，android就改变了权限的管理模式。对于一些涉及用户隐私的权限则需要用户的授权才可以使用。在此之前，开发者只需要在AndroidManifest中注册权限即可，但是现在除了注册还需要进行手动的授权。
对于很多开发者来说，这一步似乎挺麻烦的，但是又不得不做。今天我要记录的正是如何通过最简单有效的方式来解决这个让人心烦的问题。
首先我们需要了解权限申请的几个相关方法。

---

项目地址：[https://github.com/KCrason/PermissionGranted.git](https://github.com/KCrason/PermissionGranted.git)
项目演示：
![示例演示](http://upload-images.jianshu.io/upload_images/1860505-4d4b41b451905bff.gif?imageMogr2/auto-orient/strip)


---

##### 一，请求权限的方法
```
ActivityCompat.requestPermissions(final @NonNull Activity activity,final @NonNull String[] permissions, final @IntRange(from = 0) int requestCode)
```
从该方法可以看出，可以一次申请多个权限，多个权限由数组构成。而后面的requestCode则是权限请求的请求码，该字段在请求结果处理的方法```onRequestPermissionsResult```中起作用。
##### 二，检查是否已经授权相关权限
```
ContextCompat.checkSelfPermission(@NonNull Context context, @NonNull String permission)
```
从该方法的参数上来看，只能接收单个的权限，然后再去判断是否已经授权，该处判断是否已经授权主要是看```checkSelfPermission```方法返回的常量值是否等于```PackageManager.PERMISSION_GRANTED```，如果不等于该值，则说明当前所需的权限还没有进行用户授权，此时需要进行用户授权。
##### 三，检查是否需要提示用户对该权限的授权进行说明，返回boolean类型
```
ActivityCompat.shouldShowRequestPermissionRationale(@NonNull Activity activity, @NonNull String permission)
```
关于这个方法，网上一直有很多争论，这个方法到底该怎么用，刚开始我也是一头雾水，因为本身官方API的用意是希望该方法在弹出一次权限后，如果用户拒绝，再次弹出授权时先给用户一个说明（也就是为什么我要用这个权限）。此时的返回值为true。但是据说国内的很多OS都是直接返回false。这里就不吐槽国内OS云云了，直接说怎么办吧，在该处的处理是直接提示到设置界面进行开启授权。

##### 四，权限的授权回调结果
```
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     ......
}
```
该方法就是在进行用户授权后，用户反馈的结果回调。不管是成功还是失败，都会在这里有一个回调结果，同样的，返回的结果也是一个int型的数组```grantResults```，该值保存的就是前面讲到的```PackageManager.PERMISSION_GRANTED```，而requestCode就是```requestPermissions```方法中的requestCode。需要注意的是，由于该方法是```FragmentActivity```的一个方法。因此，对于回调的结果就必然要在```FragmentActivity```类或器子类中进行处理。

---

好了，重要的方法已经讲完了，接下来讲一下如何既能简单快捷封装，又不影响外部调用者的便捷。前面讲到，既然回调的方法需要在```FragmentActivity```或其子类中，那么索性就将所有的请求处理全部交由```AppCompatActivity```，我们构建了一个名称为PermissionActivity的类，该类专门用于处理权限相关的内容，同时，这个activity的主题应该设置为透明。因为我们不能因为请求权限而影响其他的界面显示。所以我们给该activity设置的style为：
```
<style name="NoTitleTranslucentTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="android:windowNoTitle">true</item>
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:windowIsTranslucent">true</item>
</style>
```
，当我们需要请求某一个或者某一组权限的时候，我们通过Intent去传递即可。而请求权限的结果回调，则直接在发起请求的那个activity的```onActivityResult```中处理即可。其中PermissionActivity类具体的逻辑处理可以参考[源代码](https://github.com/KCrason/PermissionGranted.git)

---

最后，来看一下使用示例：比如我需要请求相机和录音权限
- 第一步，发起请求
```
startActivityForResult(
new Intent(this, PermissionActivity.class).putExtra(PermissionActivity.KEY_PERMISSIONS_ARRAY,
new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}), PermissionActivity.CALL_BACK_PERMISSIO
```
 - 第二步，处理结果。
```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionActivity.CALL_BACK_PERMISSION_REQUEST_CODE){
            switch (resultCode){
                case PermissionActivity.CALL_BACK_RESULT_CODE_SUCCESS:
                    Toast.makeText(this,"权限申请成功！",Toast.LENGTH_SHORT).show();
                    break;
                case PermissionActivity.CALL_BACK_RESULE_CODE_FAILURE:
                    Toast.makeText(this,"权限申请失败！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
```
当然，现在网络上不乏有很多框架来处理这个问题，但是我认为，很多时候，作为一个开发者，能尽量少用框架的时候就尽量少用，能自己写总比老想着用别人的好，想想在一个项目中加那么多compile不觉得后背发凉么？O(∩_∩)O哈哈~
