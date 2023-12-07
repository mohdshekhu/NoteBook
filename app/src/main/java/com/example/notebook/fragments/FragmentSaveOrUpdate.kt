package com.example.notebook.fragments

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.notebook.R
import com.example.notebook.activities.MainActivity
import com.example.notebook.databinding.BottomSheetLayoutBinding
import com.example.notebook.databinding.FragmentSaveOrUpdateBinding
import com.example.notebook.modal.Note
import com.example.notebook.utils.hideKeyboard
import com.example.notebook.viewModal.NoteActivityViewModal
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class FragmentSaveOrUpdate : Fragment(R.layout.fragment_save_or_update) {

private lateinit var binding : FragmentSaveOrUpdateBinding
private lateinit var navController: NavController
private var note : Note? = null
private var color = -1
    private lateinit var result : String
private val noteActivityViewModal : NoteActivityViewModal by activityViewModels()
private val currentDate = SimpleDateFormat.getDateInstance().format(Date())
private val job = CoroutineScope(Dispatchers.Main)
private val arg : FragmentSaveOrUpdateArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Toast.makeText(activity, "SaveUpdate Calls" ,Toast.LENGTH_LONG).show()
        val animation = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration = 300L
        }
        sharedElementEnterTransition  = animation
        sharedElementReturnTransition = animation


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSaveOrUpdateBinding.bind(view)
        navController = Navigation.findNavController(view)
        val activity = activity as MainActivity

        binding.saveNote.setOnClickListener {
            saveNote()
        }

//        Log.d("CheckColor", activity.window.statusBarColor.toString())

        ViewCompat.setTransitionName(
            binding.noteContentFragmentParent ,
            "recyclerView_${arg.note?.id}"
        )


        binding.backBtn.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

        try {
            binding.edtNoteContent.setOnFocusChangeListener{_,hasFocus ->
                if (hasFocus){
                    binding.bottomBar.visibility = View.VISIBLE
                    binding.edtNoteContent.setStylesBar(binding.styleBar)
                }else{
                    binding.bottomBar.visibility = View.GONE
                }
            }
        }catch (e:Throwable){
            Log.d("TAG" , e.printStackTrace().toString())
        }

        binding.fabColorPick.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext() , R.style.BottomSheetDialogSheet)

            val bottomSheetView : View = layoutInflater.inflate(R.layout.bottom_sheet_layout ,null)

            with(bottomSheetDialog){
                setContentView(bottomSheetView)
                show()
            }

            val bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView)
            bottomSheetBinding.apply {
                colorPicker.apply {
                    setSelectedColor(color)
                    setOnColorSelectedListener {
                        value->
                        color = value
                        binding.apply {
                            noteContentFragmentParent.setBackgroundColor(color)
                            toolbarFragmentNoteContent.setBackgroundColor(color)
                            bottomBar.setBackgroundColor(color)
                            if (color!=Color.WHITE){
                                activity.window.statusBarColor = color
                            }else{
                                activity.window.statusBarColor = -6382179 // To make the default StatusBar
                            }
                        }
                        bottomSheetBinding.bottomSheetParent.setCardBackgroundColor(color)

                    }
                }
                bottomSheetParent.setCardBackgroundColor(color)
            }
            bottomSheetView.post {
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        //Opens With Existing Note Item
        setUpNote()

    }

    private fun setUpNote() {
        val note = arg.note
        val title = binding.edtTitle
        val content = binding.edtNoteContent
        val lastEdited = binding.lastEdited

        if (note == null){
            binding.lastEdited.text = getString(R.string.edited_on, SimpleDateFormat.getDateInstance().format(Date()))
        }
        if (note!=null){
            title.setText(note.title)
            content.renderMD(note.content)
            lastEdited.text = getString(R.string.edited_on , note.date)
            color = note.color
            binding.apply {
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(color)
                }
                toolbarFragmentNoteContent.setBackgroundColor(color)
                bottomBar.setBackgroundColor(color)

            }
            activity?.window?.statusBarColor = note.color
        }



    }

    private fun saveNote() {
        if(binding.edtNoteContent.text.toString().isEmpty() || binding.edtTitle.text.toString().isEmpty() ){
            Toast.makeText(activity , "Something is Empty" , Toast.LENGTH_SHORT).show()
        } else{
            note = arg.note
            when(note) {
                null -> {
                    noteActivityViewModal.saveNote(
                        Note(
                            0,
                            binding.edtTitle.text.toString(),
                            binding.edtNoteContent.getMD(),
                            currentDate,
                            color
                        )
                    )

                    result = "Note Saved"
                    setFragmentResult(
                        "key",
                        bundleOf("bundleKey" to result)
                    )

                    navController.navigate(FragmentSaveOrUpdateDirections.actionFragmentSaveOrUpdateToNoteFragment())

                }
                else ->{
                    //update note
                    updateNote()
                    navController.popBackStack()
                }
            }

        }
    }

    private fun updateNote() {
        Log.d("CheckNote","Update calls")
        if (note!=null){
            noteActivityViewModal.updateNote(
                Note(
                    note!!.id ,
                    binding.edtTitle.text.toString() ,
                    binding.edtNoteContent.getMD() ,
                    currentDate,
                    color
                )
            )
//            Toast.makeText(requireContext(),"Updated Calls",Toast.LENGTH_LONG).show()

        }

    }

}