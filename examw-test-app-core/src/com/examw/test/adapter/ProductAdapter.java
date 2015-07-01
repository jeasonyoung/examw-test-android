package com.examw.test.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.model.sync.ProductModel;
/**
 * 产品列表适配器。
 * 
 * @author jeasonyoung
 * @since 2015年6月30日
 */
public class ProductAdapter extends BaseAdapter {
	private static final String TAG = "ProductAdapter";
	private Context context;
	private List<ProductModel> dataSource;
	/**
	 * 构造函数。
	 * @param context
	 * 上下文。
	 * @param dataSource
	 * 数据源。
	 */
	public ProductAdapter(Context context,List<ProductModel> dataSource) {
		Log.d(TAG, "初始化...");
		this.context = context;
		this.dataSource = dataSource;
	}
	/*
	 * 获取数据总数。
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return this.dataSource == null ? 0 : this.dataSource.size();
	}
	/*
	 * 获取数据对象。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return this.dataSource.get(position);
	}
	/*
	 * 重载。
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	/*
	 * 重载创建行。
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "准备创建行..." + position);
		ItemViewWrapper viewWrapper = null;
		if(convertView == null){
			Log.d(TAG, "创建新行:" + position);
			convertView = LayoutInflater.from(this.context).inflate(R.layout.ui_switch_product_item, parent, false);
			//初始化
			viewWrapper = new ItemViewWrapper();
			//1.产品名称
			viewWrapper.setName((TextView)convertView.findViewById(R.id.product_item_name));
			//2.试卷数
			viewWrapper.setPapers((TextView)convertView.findViewById(R.id.product_item_papers));
			//3.试题数
			viewWrapper.setItems((TextView)convertView.findViewById(R.id.product_item_items));
			//4.原价
			viewWrapper.setPrice((TextView)convertView.findViewById(R.id.product_item_price));
			//5.优惠价
			viewWrapper.setDiscount((TextView)convertView.findViewById(R.id.product_item_discount));
			//存储包装对象
			convertView.setTag(viewWrapper);
		}else {
			Log.d(TAG, "重复使用行:" + position);
			//重用包装对象
			viewWrapper = (ItemViewWrapper)convertView.getTag();
		}
		//装载数据
		Log.d(TAG, "装载行数据:" + position);
		ProductModel data = (ProductModel)this.getItem(position);
		//1.产品名称
		viewWrapper.getName().setText(data.getName());
		//2.试卷数
		viewWrapper.getPapers().setText("试卷数:" + data.getPapers());
		//3.试题数
		viewWrapper.getItems().setText("试题数:" + data.getItems());
		//4.原价
		viewWrapper.getPrice().setText("原价:" + data.getPrice());
		//5.优惠价
		viewWrapper.getDiscount().setText("优惠价:" + data.getDiscount());
		//返回行视图
		return convertView;
	}
	/**
	 * 行包装类。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月1日
	 */
	private class ItemViewWrapper{
		private TextView name,papers,items,price,discount;
		/**
		 * 获取产品名称。
		 * @return 产品名称。
		 */
		public TextView getName() {
			return name;
		}
		/**
		 * 设置产品名称。
		 * @param name 
		 *	  产品名称。
		 */
		public void setName(TextView name) {
			this.name = name;
		}
		/**
		 * 获取试卷数。
		 * @return 试卷数。
		 */
		public TextView getPapers() {
			return papers;
		}
		/**
		 * 设置试卷数。
		 * @param papers 
		 *	  试卷数。
		 */
		public void setPapers(TextView papers) {
			this.papers = papers;
		}
		/**
		 * 获取试题数。
		 * @return 试题数。
		 */
		public TextView getItems() {
			return items;
		}
		/**
		 * 设置试题数。
		 * @param items 
		 *	  试题数。
		 */
		public void setItems(TextView items) {
			this.items = items;
		}
		/**
		 * 获取产品原价。
		 * @return 产品原价。
		 */
		public TextView getPrice() {
			return price;
		}
		/**
		 * 设置产品原价。
		 * @param price 
		 *	  产品原价。
		 */
		public void setPrice(TextView price) {
			if(price != null){
				//中间加横线
				price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			}
			this.price = price;
		}
		/**
		 * 获取产品优惠价。
		 * @return 产品优惠价。
		 */
		public TextView getDiscount() {
			return discount;
		}
		/**
		 * 设置产品优惠价。
		 * @param discount 
		 *	  产品优惠价。
		 */
		public void setDiscount(TextView discount) {
			this.discount = discount;
		}
	}
}