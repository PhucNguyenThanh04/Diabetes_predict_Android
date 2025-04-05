package com.example.diabetes_api

import android.os.Bundle
import android.widget.ArrayAdapter

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.diabetes_api.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private  var pregnancies: Float? = null
    private  var glucose : Float? = null
    private var bloodPressure: Float? = null
    private var skinThickness: Float? = null
    private var insulin: Float? = null
    private var bmi: Float? = null
    private var diabetesPedigreeFunction: Float? =null
    private var age: Int? = null
    private var smoker: Int? = null
    private lateinit var symptoms: String
    private lateinit var bmi_category: String
    private lateinit var result: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val smokingList = arrayOf("Yes", "No")
        val adaptersmoking = ArrayAdapter(this, android.R.layout.simple_list_item_1, smokingList)
        val autoCompletesmoking= binding.listofsmoking
        autoCompletesmoking.setAdapter(adaptersmoking)

        val symptomsList = arrayOf("Severe symptoms", "Under treatment", "Mild symptoms", "No symptoms")
        val adaptersymptoms = ArrayAdapter(this, android.R.layout.simple_list_item_1, symptomsList)
        val autoCompletesymptoms = binding.listofsymptoms
        autoCompletesymptoms.setAdapter(adaptersymptoms)

        val bmiList = arrayOf("Normal", "Underweight", "Overweight", "Obese")
        val adapterbmi = ArrayAdapter(this, android.R.layout.simple_list_item_1, bmiList)
        val autoCompletebmi = binding.listofbmicategory
        autoCompletebmi.setAdapter(adapterbmi)



        binding.btnSubmitData.setOnClickListener {
            pregnancies = binding.Pregnancies.text.toString().toFloatOrNull()
            glucose = binding.Glucose.text.toString().toFloatOrNull()
            bloodPressure = binding.BloodPressure.text.toString().toFloatOrNull()
            skinThickness = binding.SkinThickness.text.toString().toFloatOrNull()
            insulin = binding.Insulin.text.toString().toFloatOrNull()
            bmi = binding.BMI.text.toString().toFloatOrNull()
            diabetesPedigreeFunction = binding.PedigreeFunction.text.toString().toFloatOrNull()
            age = binding.Age.text.toString().toIntOrNull()
            var checksmoker = autoCompletesmoking.text.toString()
            smoker = if (checksmoker == "Yes") {
                1
            } else {
                0
            }
            symptoms = autoCompletesymptoms.text.toString()
            bmi_category = autoCompletebmi.text.toString()

            if (pregnancies == null || glucose == null || bloodPressure == null || checksmoker.isBlank()
                        || skinThickness == null || insulin == null || bmi == null || diabetesPedigreeFunction == null
                        || age == null || symptoms.isBlank() || bmi_category.isBlank()){
                Toast.makeText(this@MainActivity, "Vui long nhap day du thong tin", Toast.LENGTH_SHORT).show()
            }else{

                val inputData = InputData(
                    Pregnancies = pregnancies ?: 0f,
                    Glucose = glucose ?: 0f,
                    BloodPressure = bloodPressure ?: 0f,
                    SkinThickness = skinThickness ?: 0f,
                    Insulin = insulin ?: 0f,
                    BMI = bmi ?: 0f,
                    DiabetesPedigreeFunction = diabetesPedigreeFunction ?: 0f,
                    Age = age ?: 0,
                    Smoker = smoker ?: 0    ,
                    Notes = listOf(symptoms),
                    BMI_Category = listOf(bmi_category)
                )

                // Gọi API để dự đoán
                ApiClient.apiService.predict(inputData).enqueue(object : Callback<PredictionResponse> {
                    override fun onResponse(call: Call<PredictionResponse>, response: Response<PredictionResponse>) {
                        if (response.isSuccessful) {
                            result = response.body()?.message.toString()
//                        Toast.makeText(this@MainActivity, result, Toast.LENGTH_SHORT).show()
                            val builder = AlertDialog.Builder(this@MainActivity)
                            builder.setTitle("Kết quả dự đoán bệnh tiểu đường")
                                .setMessage(result)
                                .setCancelable(false)
                                .setPositiveButton("OK") { dialog, id ->
                                    dialog.dismiss()
                                }
                            val alert = builder.create()
                            alert.show()
                        } else {
                            Toast.makeText(this@MainActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

    }
}




