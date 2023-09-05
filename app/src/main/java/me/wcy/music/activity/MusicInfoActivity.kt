package me.wcy.music.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import me.wcy.common.ext.toast
import me.wcy.music.R
import me.wcy.music.const.Extras
import me.wcy.music.const.RequestCode
import me.wcy.music.model.Music
import me.wcy.music.utils.CoverLoader
import me.wcy.music.utils.FileUtils
import me.wcy.music.utils.ImageUtils
import me.wcy.music.utils.TimeUtils
import me.wcy.music.utils.binding.Bind
import me.wcy.music.utils.id3.ID3TagUtils
import me.wcy.music.utils.id3.ID3Tags
import java.io.File
import java.util.Locale

class MusicInfoActivity : BaseActivity(), View.OnClickListener {
    @Bind(R.id.iv_music_info_cover)
    private val ivCover: ImageView? = null

    @Bind(R.id.et_music_info_title)
    private val etTitle: EditText? = null

    @Bind(R.id.et_music_info_artist)
    private val etArtist: EditText? = null

    @Bind(R.id.et_music_info_album)
    private val etAlbum: EditText? = null

    @Bind(R.id.tv_music_info_duration)
    private val tvDuration: TextView? = null

    @Bind(R.id.tv_music_info_file_name)
    private val tvFileName: TextView? = null

    @Bind(R.id.tv_music_info_file_size)
    private val tvFileSize: TextView? = null

    @Bind(R.id.tv_music_info_file_path)
    private val tvFilePath: TextView? = null
    private var mMusic: Music? = null
    private var mMusicFile: File? = null
    private var mCoverBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_info)
        mMusic = intent.getSerializableExtra(Extras.MUSIC) as Music?
        if (mMusic == null || mMusic?.type != Music.Type.LOCAL) {
            finish()
            return
        }
        mMusicFile = File(mMusic!!.path)
        mCoverBitmap = CoverLoader.get().loadThumb(mMusic)
        initView()
    }

    private fun initView() {
        ivCover!!.setImageBitmap(mCoverBitmap)
        ivCover.setOnClickListener(this)
        etTitle!!.setText(mMusic!!.title)
        etTitle!!.setSelection(etTitle.length())
        etArtist!!.setText(mMusic!!.artist)
        etArtist!!.setSelection(etArtist.length())
        etAlbum!!.setText(mMusic!!.album)
        etAlbum!!.setSelection(etAlbum.length())
        tvDuration!!.text = TimeUtils.formatTime("mm:ss", mMusic!!.duration)
        tvFileName!!.setText(mMusic!!.fileName)
        tvFileSize!!.text = String.format(
            Locale.getDefault(),
            "%.2fMB",
            FileUtils.b2mb(mMusic!!.fileSize.toInt())
        )
        tvFilePath!!.text = mMusicFile!!.parent
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_music_info, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save) {
            save()
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
//        PermissionReq.with(this)
//            .permissions(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//            .result(object : PermissionReq.Result {
//                override fun onGranted() {
//                    ImageUtils.startAlbum(this@MusicInfoActivity)
//                }
//
//                override fun onDenied() {
//                    toast(R.string.no_permission_select_image)
//                }
//            })
//            .request()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }
        if (requestCode == RequestCode.REQUEST_ALBUM && data != null) {
            ImageUtils.startCorp(this, data.data)
        } else if (requestCode == RequestCode.REQUEST_CORP) {
            val corpFile = File(FileUtils.getCorpImagePath(this))
            if (!corpFile.exists()) {
                toast("图片保存失败")
                return
            }
            mCoverBitmap = BitmapFactory.decodeFile(corpFile.path)
            ivCover!!.setImageBitmap(mCoverBitmap)
            corpFile.delete()
        }
    }

    private fun save() {
        if (!mMusicFile!!.exists()) {
            toast("歌曲文件不存在")
            return
        }
        val id3Tags = ID3Tags.Builder().setCoverBitmap(mCoverBitmap)
            .setTitle(etTitle!!.text.toString())
            .setArtist(etArtist!!.text.toString())
            .setAlbum(etAlbum!!.text.toString())
            .build()
        ID3TagUtils.setID3Tags(mMusicFile, id3Tags, false)

        // 刷新媒体库
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mMusicFile))
        sendBroadcast(intent)
        toast("保存成功")
    }

    companion object {
        fun start(context: Context?, music: Music?) {
            val intent = Intent(context, MusicInfoActivity::class.java)
            intent.putExtra(Extras.MUSIC, music)
            context!!.startActivity(intent)
        }
    }
}