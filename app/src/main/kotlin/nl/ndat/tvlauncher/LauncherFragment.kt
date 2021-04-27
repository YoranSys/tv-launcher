package nl.ndat.tvlauncher

import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.media.tv.TvInputManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.WatchNextProgram
import nl.ndat.tvlauncher.databinding.FragmentLauncherBinding
import nl.ndat.tvlauncher.utils.createSwitchIntent
import nl.ndat.tvlauncher.utils.loadBanner
import nl.ndat.tvlauncher.utils.loadPreferredLabel

class LauncherFragment : Fragment() {
	private var _binding: FragmentLauncherBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentLauncherBinding.inflate(inflater, container, false)
		binding.apps.requestFocus()
		addEventListeners()
		addApps()
		addIdk()
		return binding.root
	}

	private fun addIdk() {
		registerForActivityResult(ActivityResultContracts.RequestPermission()) {
			println("Permission state -> $it")
			val cursor = requireContext().contentResolver.query(
				TvContractCompat.WatchNextPrograms.CONTENT_URI,
				WatchNextProgram.PROJECTION,
				null,
				null,
				null
			)

			val programs = mutableListOf<WatchNextProgram>()
			if (cursor != null && cursor.moveToFirst()) {
				do {
					programs.add(WatchNextProgram.fromCursor(cursor))
				} while (cursor.moveToNext())
			}

			println(programs)
		}.launch("com.android.providers.tv.permission.ACCESS_ALL_EPG_DATA")
	}

	private fun addEventListeners() {
		binding.button.setOnFocusChangeListener { _, hasFocus ->
			val color = ContextCompat.getColor(requireContext(), if (hasFocus) R.color.lb_tv_white else R.color.lb_grey)
			val animator = ValueAnimator.ofArgb(binding.button.imageTintList!!.defaultColor, color)
			animator.addUpdateListener {
				binding.button.imageTintList = ColorStateList.valueOf(it.animatedValue as Int)
			}
			animator.duration = 200
			animator.start()
		}

		binding.button.setOnClickListener {
			startActivity(Intent(Settings.ACTION_SETTINGS))
		}
	}

	@OptIn(ExperimentalStdlibApi::class)
	private fun addApps() {
		val intent = Intent(Intent.ACTION_MAIN, null).apply {
			addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
		}

		val packageManager = requireContext().packageManager
		val tvInputManager = requireContext().getSystemService<TvInputManager>()
		val activities = packageManager.queryIntentActivities(intent, 0)
		val tvInputs = tvInputManager?.tvInputList.orEmpty()

		val apps = buildList {
			// Add leanback apps
			activities
				.sortedBy {
					it.activityInfo.loadLabel(packageManager).toString()
				}
				.map { resolveInfo ->
					val banner = resolveInfo.activityInfo.loadBanner(packageManager)
						?: resolveInfo.activityInfo.loadIcon(packageManager)

					val appIntent =
						packageManager.getLeanbackLaunchIntentForPackage(resolveInfo.activityInfo.packageName)
							?: packageManager.getLaunchIntentForPackage(resolveInfo.activityInfo.packageName)

					AppInfo(
						label = resolveInfo.activityInfo.loadLabel(packageManager).toString(),
						banner = banner,
						intent = appIntent,
					)
				}
				.let { addAll(it) }

			// Add inputs as app
			tvInputs
				.map { tvInputInfo ->
					AppInfo(
						label = tvInputInfo.loadPreferredLabel(requireContext()),
						banner = tvInputInfo.loadBanner(requireContext()),
						intent = tvInputInfo.createSwitchIntent()
					)
				}
				.let { addAll(it) }

		}

		binding.apps.adapter = AppListAdapter(requireContext(), apps)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
