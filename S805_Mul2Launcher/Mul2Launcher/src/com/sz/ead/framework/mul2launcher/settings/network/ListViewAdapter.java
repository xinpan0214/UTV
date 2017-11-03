/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: ListViewAdapter.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:52:57
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.R;


public class ListViewAdapter extends BaseAdapter {
	
	View[] itemViews;   
	Context m_context;
	
	String[] m_wifiNames;
	boolean[] m_isLock;
	boolean[] m_curConnecting;
	int m_curConnecting1;
	int[] m_level;
	ProgressBar m_progressBar;
	
	int m_size;
	private LayoutInflater mInflater;
	ImageView m_wifiConnected;
	public static final String TAG = "network";
	
	boolean m_isConnect;
	
    public ListViewAdapter() {  
    }

    /**
     * 设置listview信息
     * @Title: setListView
     * @Description: TODO
     * @param wifiName
     * @param lock
     * @param level
     * @param isConnect
     * @param curConnecting1
     * @param context
     * @return: void
     */
    public void setListView(String[] wifiName, boolean[] lock,int[] level, boolean isConnect,int curConnecting1,Context context)
    {
    	m_wifiNames = wifiName;
    	m_isLock = lock;
    	m_level = level;
    	m_isConnect = isConnect;
    	m_curConnecting1 = curConnecting1;
    	this.m_context = context;
    	m_size = wifiName.length;
    }
    
    @Override
    public int getCount() {  
        return m_size;  
    }

    @Override
    public Object getItem(int position) {
        return position;
    }  

    @Override
    public long getItemId(int position) {  
        return position;  
    }  

    /**
     * 获取view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {  
  
    	ViewHolder holder;
        if (convertView == null) 
        {
            convertView=LayoutInflater.from(m_context).inflate(R.layout.settings_network_listviewitem, null);  
            
            holder = new ViewHolder();
            
            holder.wifiName = (TextView)convertView.findViewById(R.id.id_setting_network_wifi_name);
            holder.lockImage = (ImageView)convertView.findViewById(R.id.id_setting_wifi_lockImage);
            holder.title = (TextView) convertView.findViewById(R.id.id_setting_network_wifi_name );
            holder.progressBar = (ProgressBar)convertView.findViewById(R.id.id_setting_network_linking_progressbar);
            holder.signalimage = (ImageView)convertView.findViewById(R.id.id_setting_network_wifi_signal);
            holder.connectImage = (ImageView)convertView.findViewById(R.id.id_setting_network_wifi_connect);
            
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        
        
        holder.progressBar.setVisibility(View.GONE);
        holder.connectImage.setVisibility(View.GONE);
        
        holder.wifiName.setText(m_wifiNames[position]);
        
        if (m_curConnecting1 == position)
        {
        	holder.progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
        	holder.progressBar.setVisibility(View.GONE);
        }
        
        if (m_isLock[position])
        {
        	holder.lockImage.setVisibility(View.VISIBLE);
        }
        else
        {
        	holder.lockImage.setVisibility(View.GONE);
        }
        
        if (0 == m_level[position])
        {
        	holder.signalimage.setImageResource(R.drawable.settings_network_wifi_wifi0_uf);
        }
        else if (1 == m_level[position])
        {
        	holder.signalimage.setImageResource(R.drawable.settings_network_wifi_wifi1_uf);
        }
        else if (2 == m_level[position])
        {
        	holder.signalimage.setImageResource(R.drawable.settings_network_wifi_wifi2_uf);
        }
        else if (3 == m_level[position])
        {
        	holder.signalimage.setImageResource(R.drawable.settings_network_wifi_wifi3_uf);
        }
        
        if (0 == position)
        {
        	if (m_isConnect)
        	{
        		holder.connectImage.setVisibility(View.VISIBLE);
        	}
        	else
        	{
        		holder.connectImage.setVisibility(View.GONE);
        	}
        }
        
        return convertView;
    }  
    
    /**
     * 设置是否显示加载框
     * @Title: setWaittingShow
     * @Description: TODO
     * @param isShow
     * @return: void
     */
    public void setWaittingShow(boolean isShow)
    {
    	if (isShow)
    	{
    		this.m_progressBar.setVisibility(View.VISIBLE);
    	}
    	else
    	{
    		this.m_progressBar.setVisibility(View.GONE);
    	}
    }
    
    public final class ViewHolder {  
    	public TextView title;
        public TextView wifiName;  
        public ImageView lockImage;
        public ImageView signalimage;
        public ProgressBar progressBar;
        public ImageView connectImage;
    }  
}  