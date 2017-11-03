/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\demo\\¹Ì¼þ\\code\\UTV_S805\\S805_Mul2Launcher\\Mul2Launcher\\src\\com\\sz\\ead\\framework\\upgrademanager\\IUpgradeBgAIDL.aidl
 */
package com.sz.ead.framework.upgrademanager;
public interface IUpgradeBgAIDL extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.sz.ead.framework.upgrademanager.IUpgradeBgAIDL
{
private static final java.lang.String DESCRIPTOR = "com.sz.ead.framework.upgrademanager.IUpgradeBgAIDL";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.sz.ead.framework.upgrademanager.IUpgradeBgAIDL interface,
 * generating a proxy if needed.
 */
public static com.sz.ead.framework.upgrademanager.IUpgradeBgAIDL asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.sz.ead.framework.upgrademanager.IUpgradeBgAIDL))) {
return ((com.sz.ead.framework.upgrademanager.IUpgradeBgAIDL)iin);
}
return new com.sz.ead.framework.upgrademanager.IUpgradeBgAIDL.Stub.Proxy(obj);
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
case TRANSACTION_getVersion:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getVersion();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.sz.ead.framework.upgrademanager.IUpgradeBgCallback _arg0;
_arg0 = com.sz.ead.framework.upgrademanager.IUpgradeBgCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.sz.ead.framework.upgrademanager.IUpgradeBgCallback _arg0;
_arg0 = com.sz.ead.framework.upgrademanager.IUpgradeBgCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_startCheckVersion:
{
data.enforceInterface(DESCRIPTOR);
this.startCheckVersion();
reply.writeNoException();
return true;
}
case TRANSACTION_startDownload:
{
data.enforceInterface(DESCRIPTOR);
this.startDownload();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.sz.ead.framework.upgrademanager.IUpgradeBgAIDL
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
@Override public java.lang.String getVersion() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getVersion, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void registerCallback(com.sz.ead.framework.upgrademanager.IUpgradeBgCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallback(com.sz.ead.framework.upgrademanager.IUpgradeBgCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void startCheckVersion() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startCheckVersion, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void startDownload() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startDownload, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getVersion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_startCheckVersion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_startDownload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public java.lang.String getVersion() throws android.os.RemoteException;
public void registerCallback(com.sz.ead.framework.upgrademanager.IUpgradeBgCallback callback) throws android.os.RemoteException;
public void unregisterCallback(com.sz.ead.framework.upgrademanager.IUpgradeBgCallback callback) throws android.os.RemoteException;
public void startCheckVersion() throws android.os.RemoteException;
public void startDownload() throws android.os.RemoteException;
}
