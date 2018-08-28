package com.seedit.diet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.item_gallery_view.*

private const val INTENT_KEY_URI_LIST= "uri list"
private const val INTENT_KEY_POSITION= "position"

fun Context.startGalleryActivity(list: List<Uri>, position: Int=0) =
	startActivity(Intent(this,GalleryActivity::class.java).apply {
		putExtra(INTENT_KEY_URI_LIST, ArrayList(list))
		putExtra(INTENT_KEY_POSITION,position)
	})

class GalleryActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		BigImageViewer.initialize(GlideImageLoader.with(applicationContext))
		setContentView(R.layout.activity_gallery)

		val imageUrlsList=intent.getParcelableArrayListExtra<Uri>(INTENT_KEY_URI_LIST)

		recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
		recycler.adapter = GalleryAdapter(imageUrlsList)

		val gravityPagerSnapHelper = GravityPagerSnapHelper(Gravity.START, true)
		gravityPagerSnapHelper.attachToRecyclerView(recycler)
	}

	class GalleryAdapter(imageUrlsList: List<Uri>): RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>(), List<Uri> by imageUrlsList
	{
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
			GalleryViewHolder(View.inflate(parent.context, R.layout.item_gallery_view, null))

		override fun getItemCount()=size

		override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) = holder.bind(get(position))

		override fun onViewRecycled(holder: GalleryViewHolder) {
			super.onViewRecycled(holder)
			holder.clear()
		}

		override fun onViewAttachedToWindow(holder: GalleryViewHolder) {
			super.onViewAttachedToWindow(holder)
			if (holder.hasNoImage())
				holder.rebind()
		}

		class GalleryViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
		{
			private lateinit var imageUrl: Uri

			init {
				//itemImage.setProgressIndicator(ProgressPieIndicator())
				itemImage.setTapToRetry(true)
				//itemImage.setImageViewFactory(viewFactory)
			}

			fun bind(imageUrl: Uri) {
				this.imageUrl=imageUrl
				itemImage.showImage(imageUrl)
			}

			fun rebind() {
				itemImage.showImage(imageUrl)
			}

			fun clear() {
				val ssiv = itemImage.ssiv
				ssiv?.recycle()
			}

			fun hasNoImage(): Boolean {
				val ssiv = itemImage.ssiv
				return ssiv == null || !ssiv.hasImage()
			}
		}
	}
}
