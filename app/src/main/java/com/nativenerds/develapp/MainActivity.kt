package com.nativenerds.develapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nativenerds.develapp.AutoCode.AutoCodeFragment
import com.nativenerds.develapp.CodeAssist.CodeAssistFragment
import com.nativenerds.develapp.DeStress.DeStreessFragment
import com.nativenerds.develapp.News.NewsFragment
import com.nativenerds.develapp.ide.IDEFragment
import com.nativenerds.develapp.todo.Tasksfragment
import com.nativenerds.develapp.todo.ToDoFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)

        val homeBottomNavigationBar = findViewById<BottomNavigationView>(R.id.home_bottom_navigation_bar)

        homeBottomNavigationBar.selectedItemId = R.id.tado_bottom_nav
        replacefragment(Tasksfragment())

        homeBottomNavigationBar.setOnItemSelectedListener { item ->

            when (item.itemId)
            {
                R.id.tado_bottom_nav -> {
                    replacefragment(Tasksfragment())
                }
                R.id.code_assist_bottom_nav -> {
                    replacefragment(CodeAssistFragment())
                }

                R.id.news_bottom_nav -> {
                    replacefragment(NewsFragment())
                }

                R.id.destress_bottom_nav -> {
                    replacefragment(DeStreessFragment())
                }
                R.id.ide_bottom_nav -> {
                    replacefragment(IDEFragment())
                }
            }

            return@setOnItemSelectedListener true
        }

    }

    private fun replacefragment(fragment : Fragment)
    {
        val fragmentmanager = supportFragmentManager
        val fragmentTransaction = fragmentmanager.beginTransaction()
        fragmentTransaction.replace(R.id.Home_Activity_Fragment_Container, fragment)
        fragmentTransaction.commit()
    }
}