package com.seedit.diet

import android.arch.lifecycle.Observer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.seedit.diet.viewmodel.AnalyzeDietViewModel
import com.seedit.diet.viewmodel.getViewModel
import gun0912.tedbottompicker.GridSpacingItemDecoration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_noon_body.*

class NoonBodyActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_noon_body)

		val viewModel=getViewModel(AnalyzeDietViewModel::class.java)
		val gridLayoutManager = GridLayoutManager(this, 3)
		rcGallery.layoutManager = gridLayoutManager
		rcGallery.addItemDecoration(GridSpacingItemDecoration(gridLayoutManager.spanCount, 3, false))

		viewModel.findBody(this, Observer {list->
			if(list==null || list.isEmpty())
				return@Observer

			rcGallery.adapter=NoonBodyAdapter(list.mapNotNull {entity->
				entity.image
			})
		})
	}

	inner class NoonBodyAdapter(urlList: List<Uri>):RecyclerView.Adapter<NoonBodyAdapter.NoonBodyViewHolder>(), List<Uri> by urlList
	{
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
				NoonBodyViewHolder(View.inflate(parent.context, R.layout.tedbottompicker_grid_item, null));

		override fun getItemCount()= size

		override fun onBindViewHolder(holder: NoonBodyViewHolder, position: Int)=
				holder.bind(get(position))

		inner class NoonBodyViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer, View.OnClickListener {
            val iv_thumbnail = containerView.findViewById<ImageView>(R.id.iv_thumbnail)

            init {
                containerView.setOnClickListener(this)
            }

            fun bind(uri: Uri) {
                    Glide.with(itemView.context)
                            .load(uri)
                            .thumbnail(0.1f)
                            .apply(RequestOptions().centerCrop()
                                    .placeholder(gun0912.tedbottompicker.R.drawable.ic_gallery)
                                    .error(gun0912.tedbottompicker.R.drawable.img_error))
                            .into(iv_thumbnail)
        }

			override fun onClick(v: View?) =
				startGalleryActivity(this@NoonBodyAdapter,adapterPosition)
		}
	}
}
