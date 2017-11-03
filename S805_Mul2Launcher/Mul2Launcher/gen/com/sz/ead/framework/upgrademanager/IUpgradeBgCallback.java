/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\demo\\¹Ì¼þ\\code\\UTV_S805\\S805_Mul2Launcher\\Mul2Launcher\\src\\com\\sz\\ead\\framework\\upgrademanager\\IUpgradeBgCallback.aidl
 */
package com.sz.ead.framework.upgrademanager;
public interface IUpgradeBgCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.sz.ead.framework.upgrademanager.IUpgradeBgCallback
{
private static final java.lang.String DESCRIPTOR = "com.sz.ead.framework.upgrademanager.IUpgradeBgCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.sz.ead.framework.upgrademanager.IUpgradeBgCallback interface,
 * generating a proxy if needed.
 */
public static com.sz.ead.framework.upgrademanager.IUpgradeBgCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.sz.ead.framework.upgrademanager.IUpgradeBgCallback))) {
return ((com.sz.ead.framework.upgrademanager.IUpgradeBgCallback)iin);
}
return new com.sz.ead.framework.upgrademanager.IUpgradeBgCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onCheckVersionCallback:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onCheckVersionCallback(_arg0);
return true;
}
case TRANSACTION_onDownloadProgress:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onDownloadProgress(_arg0);
return true;
}
case TRANSACTION_onDownloadComplete:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.onDownloadComplete(_arg0, _arg1);
return true;
}
case TRANSACTION_onDownloadFailed:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onDownloadFailed(_arg0);
return true;
}
case TRANSACTION_onDownloadingTimeout:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onDownloadingTimeout(_arg0);
return true;
}
case TRANSACTION_onCheckMd5Failed:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onCheckMd5Failed(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.sz.ead.framework.upgrademanager.IUpgradeBgCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void onCheckVersionCallback(int ret) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(ret);
mRemote.transact(Stub.TRANSACTION_onCheckVersionCallback, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void onDownloadProgress(int progress) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(progress);
mRemote.transact(Stub.TRANSACTION_onDownloadProgress, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void onDownloadComplete(java.lang.String filePath, java.lang.String upgrade_version) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(filePath);
_data.writeString(upgrade_version);
mRemote.transact(Stub.TRANSACTION_onDownloadComplete, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void onDownloadFailed(java.lang.String upgrade_version) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(upgrade_version);
mRemote.transact(Stub.TRANSACTION_onDownloadFailed, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void onDownloadingTimeout(java.lang.String upgrade_version) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(upgrade_version);
mRemote.transact(Stub.TRANSACTION_onDownloadingTimeout, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void onCheckMd5Failed(java.lang.String upgrade_version) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(upgrade_version);
mRemote.transact(Stub.TRANSACTION_onCheckMd5Failed, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_onCheckVersionCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onDownloadProgress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onDownloadComplete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onDownloadFailed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onDownloadingTimeout = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onCheckMd5Failed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public void onCheckVersionCallback(int ret) throws android.os.RemoteException;
public void onDownloadProgress(int progress) throws android.os.RemoteException;
public void onDownloadComplete(java.lang.String filePath, java.lang.String upgrade_version) throws android.os.RemoteException;
public void onDownloadFailed(java.lang.String upgrade_version) throws android.os.RemoteException;
public void onDownloadingTimeout(java.lang.String upgrade_version) throws android.os.RemoteException;
public void onCheckMd5Failed(java.lang.String upgrade_version) throws android.os.RemoteException;
}
