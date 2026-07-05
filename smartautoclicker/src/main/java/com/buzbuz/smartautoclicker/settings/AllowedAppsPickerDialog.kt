/*
 * Copyright (C) 2026 Kevin Buzeau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.buzbuz.smartautoclicker.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.buzbuz.smartautoclicker.R
import com.buzbuz.smartautoclicker.core.android.application.AndroidApplicationInfo
import com.buzbuz.smartautoclicker.core.android.application.getAllAndroidApplicationsInfo
import com.buzbuz.smartautoclicker.databinding.DialogAllowedAppsPickerBinding

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import kotlinx.coroutines.launch

class AllowedAppsPickerDialog : BottomSheetDialogFragment() {

    private val viewModel: SettingsViewModel by viewModels({ requireParentFragment() })
    private var _binding: DialogAllowedAppsPickerBinding? = null
    private val binding get() = _binding!!

    private var allApps: List<AndroidApplicationInfo> = emptyList()
    private var filteredApps: List<AndroidApplicationInfo> = emptyList()
    private var currentWhitelist: Set<String> = emptySet()

    private lateinit var adapter: AppsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAllowedAppsPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pm = requireContext().packageManager
        allApps = pm.getAllAndroidApplicationsInfo().sortedBy { it.name }
        filteredApps = allApps

        adapter = AppsAdapter()
        binding.recyclerViewApps.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewApps.adapter = adapter

        binding.btnDone.setOnClickListener {
            dismiss()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.thirdPartyWhitelist.collect { whitelist ->
                    currentWhitelist = whitelist
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun filterList(query: String) {
        filteredApps = if (query.isEmpty()) {
            allApps
        } else {
            allApps.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.componentName.packageName.contains(query, ignoreCase = true)
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class AppsAdapter : RecyclerView.Adapter<AppsAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val appIcon: ImageView = view.findViewById(R.id.app_icon)
            val appName: TextView = view.findViewById(R.id.app_name)
            val appPackage: TextView = view.findViewById(R.id.app_package)
            val checkbox: CheckBox = view.findViewById(R.id.checkbox)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_allowed_app_picker, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val app = filteredApps[position]
            val packageName = app.componentName.packageName

            holder.appIcon.setImageDrawable(app.icon)
            holder.appName.text = app.name
            holder.appPackage.text = packageName

            // Avoid triggering listener during binding
            holder.checkbox.setOnCheckedChangeListener(null)
            holder.checkbox.isChecked = packageName in currentWhitelist

            holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
                val newWhitelist = currentWhitelist.toMutableSet()
                if (isChecked) {
                    newWhitelist.add(packageName)
                } else {
                    newWhitelist.remove(packageName)
                }
                viewModel.updateWhitelist(newWhitelist)
            }

            holder.itemView.setOnClickListener {
                holder.checkbox.toggle()
            }
        }

        override fun getItemCount(): Int = filteredApps.size
    }
}
