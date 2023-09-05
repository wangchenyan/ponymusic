package me.wcy.music.fragment

import android.content.ContentUris
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import me.wcy.common.ext.toast
import me.wcy.common.ext.viewBindings
import me.wcy.common.permission.PermissionCallback
import me.wcy.common.permission.Permissioner
import me.wcy.common.ui.fragment.BaseFragment
import me.wcy.music.R
import me.wcy.music.activity.MusicInfoActivity
import me.wcy.music.adapter.OnMoreClickListener
import me.wcy.music.adapter.PlaylistAdapter
import me.wcy.music.common.AppCache
import me.wcy.music.const.Keys
import me.wcy.music.const.RequestCode
import me.wcy.music.const.RxBusTags
import me.wcy.music.databinding.FragmentLocalMusicBinding
import me.wcy.music.loader.MusicLoaderCallback
import me.wcy.music.model.Music
import me.wcy.music.service.AudioPlayer
import java.io.File

/**
 * 本地音乐列表
 * Created by wcy on 2015/11/26.
 */
class LocalMusicFragment : BaseFragment(), OnItemClickListener, OnMoreClickListener {
    private val viewBinding by viewBindings<FragmentLocalMusicBinding>()

    private val lvLocalMusic: ListView? = null

    private val vSearching: TextView? = null
    private var loader: Loader<Cursor>? = null
    private var adapter: PlaylistAdapter? = null

    private var hasPermission = false

    override fun getRootView(): View {
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = PlaylistAdapter(AppCache.get().getLocalMusicList())
        adapter!!.setOnMoreClickListener(this)
        lvLocalMusic!!.adapter = adapter
        loadMusic()
    }

    override fun onResume() {
        super.onResume()
        loader?.forceLoad()
    }

    private fun loadMusic() {
        lvLocalMusic!!.visibility = View.GONE
        vSearching!!.visibility = View.VISIBLE
        Permissioner.requestStoragePermission(requireContext(), object : PermissionCallback {
            override fun invoke(granted: Boolean, shouldRationale: Boolean) {
                if (granted) {
                    hasPermission = true
                    initLoader()
                } else {
                    toast(R.string.no_permission_storage)
                    lvLocalMusic.visibility = View.VISIBLE
                    vSearching.visibility = View.GONE
                }
            }
        })
    }

    private fun initLoader() {
        loader = LoaderManager.getInstance(requireActivity()).initLoader(LOADER_ID, null,
            MusicLoaderCallback(requireContext()) { value: List<Music>? ->
                AppCache.get().getLocalMusicList().clear()
                AppCache.get().getLocalMusicList().addAll(value!!)
                lvLocalMusic!!.visibility = View.VISIBLE
                vSearching!!.visibility = View.GONE
                adapter!!.notifyDataSetChanged()
            })
    }

    @Subscribe(tags = [Tag(RxBusTags.SCAN_MUSIC)])
    fun scanMusic(`object`: Any?) {
        loader?.forceLoad()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val music: Music = AppCache.get().getLocalMusicList().get(position)
        AudioPlayer.get().addAndPlay(music)
        toast("已添加到播放列表")
    }

    override fun onMoreClick(position: Int) {
        val music: Music = AppCache.get().getLocalMusicList().get(position)
        val dialog = AlertDialog.Builder(
            requireContext()
        )
        dialog.setTitle(music.title)
        dialog.setItems(R.array.local_music_dialog) { dialog1: DialogInterface?, which: Int ->
            when (which) {
                0 -> shareMusic(music)
                1 -> requestSetRingtone(music)
                2 -> MusicInfoActivity.start(context, music)
                3 -> deleteMusic(music)
            }
        }
        dialog.show()
    }

    /**
     * 分享音乐
     */
    private fun shareMusic(music: Music) {
        val file = File(music.path)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "audio/*"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
        startActivity(Intent.createChooser(intent, getString(R.string.share)))
    }

    private fun requestSetRingtone(music: Music) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(
                context
            )
        ) {
            toast(R.string.no_permission_setting)
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + requireContext().packageName)
            startActivityForResult(intent, RequestCode.REQUEST_WRITE_SETTINGS)
        } else {
            setRingtone(music)
        }
    }

    /**
     * 设置铃声
     */
    private fun setRingtone(music: Music) {
        val uri = MediaStore.Audio.Media.getContentUriForPath(music.path)
        // 查询音乐文件在媒体库是否存在
        val cursor = requireContext().contentResolver
            .query(
                uri!!,
                null,
                MediaStore.MediaColumns.DATA + "=?",
                arrayOf(music.path),
                null
            )
            ?: return
        if (cursor.moveToFirst() && cursor.count > 0) {
            val _id = cursor.getString(0)
            val values = ContentValues()
            values.put(MediaStore.Audio.Media.IS_MUSIC, true)
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
            values.put(MediaStore.Audio.Media.IS_ALARM, false)
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            values.put(MediaStore.Audio.Media.IS_PODCAST, false)
            requireContext().contentResolver
                .update(uri, values, MediaStore.MediaColumns.DATA + "=?", arrayOf(music.path))
            val newUri = ContentUris.withAppendedId(uri, java.lang.Long.valueOf(_id))
            RingtoneManager.setActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_RINGTONE,
                newUri
            )
            toast(R.string.setting_ringtone_success)
        }
        cursor.close()
    }

    private fun deleteMusic(music: Music) {
        val dialog = AlertDialog.Builder(
            requireContext()
        )
        val title = music.title
        val msg = getString(R.string.delete_music, title)
        dialog.setMessage(msg)
        dialog.setPositiveButton(R.string.delete) { dialog1: DialogInterface?, which: Int ->
            val file = File(music.path)
            if (file.delete()) {
                // 刷新媒体库
                val intent =
                    Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + music.path))
                requireContext().sendBroadcast(intent)
            }
        }
        dialog.setNegativeButton(R.string.cancel, null)
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.REQUEST_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(
                    context
                )
            ) {
                toast(R.string.grant_permission_setting)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val position = lvLocalMusic!!.firstVisiblePosition
        val offset = if (lvLocalMusic.getChildAt(0) == null) 0 else lvLocalMusic.getChildAt(0).top
        outState.putInt(Keys.LOCAL_MUSIC_POSITION, position)
        outState.putInt(Keys.LOCAL_MUSIC_OFFSET, offset)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        lvLocalMusic!!.post {
            val position = savedInstanceState.getInt(Keys.LOCAL_MUSIC_POSITION)
            val offset = savedInstanceState.getInt(Keys.LOCAL_MUSIC_OFFSET)
            lvLocalMusic.setSelectionFromTop(position, offset)
        }
    }

    override fun onLazyDestroy() {
        super.onLazyDestroy()
        loader?.let {
            LoaderManager.getInstance(requireActivity()).destroyLoader(LOADER_ID)
            loader = null
        }
    }

    companion object {
        private const val LOADER_ID = 0x111
    }
}