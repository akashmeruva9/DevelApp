package com.nativenerds.develapp.ide

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nativenerds.develapp.R
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class IDEFragment : Fragment(R.layout.fragment_i_d_e) {

    var tvResult: TextView? = null
    var etInput: EditText? = null
    var btnSubmit: Button? = null

    var api = APIClient.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvResult = requireActivity().findViewById<TextView>(R.id.tv_result)
        etInput = requireActivity().findViewById<EditText>(R.id.et_input)
        btnSubmit = requireActivity().findViewById<Button>(R.id.btn_submit)
    }

    fun runCompile(view: View?) {
        val execute: Call<String> = api.api.execute(PostData(etInput!!.text.toString()))
        tvResult!!.text = "Loading..."
        execute.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>?, response: Response<String?>) {
                tvResult!!.text = ""
                try {
                    if (response.isSuccessful()) {
                        val jsonObject = JSONObject(response.body())
                        val output: String = jsonObject.getString("output")
                        tvResult!!.text = output
                    } else {
                        showSnackBar(response.errorBody().toString())
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showSnackBar("Gagal Parsing JSON : " + e.message)
                }
            }

            override fun onFailure(call: Call<String?>?, t: Throwable) {
                tvResult!!.text = "Failed"
                showSnackBar("Gagal : " + t.message)
            }
        })
    }

    private fun showSnackBar(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}