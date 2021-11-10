package com.paulo.miaudota.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paulo.miaudota.InputFilterMinMax;
import com.paulo.miaudota.Models.Cidade;
import com.paulo.miaudota.Models.Estado;
import com.paulo.miaudota.R;
import com.paulo.miaudota.Models.User;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextFullName,editTextCPF,editTextEmail,editTextPassword,editTextDataNascimento, editTextDdd, editTextNumCelular;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private ProgressBar progressBar;
    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    private String dataN, dataAtual, ibgeEstados, cidadeReg, ufReg;;
    private Spinner spinnerEstados;
    private SearchableSpinner spinnerCidades = null;
    private ArrayList<String> estadosSpinner = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        ImageView logo =  findViewById(R.id.logoRegister);
        logo.setOnClickListener(this);

        Button register = findViewById(R.id.btnRegister);
        register.setOnClickListener(this);

        editTextFullName = findViewById(R.id.nomeCadastro);
        editTextCPF = findViewById(R.id.cpfCadastro);
        editTextEmail = findViewById(R.id.emailCadastro);
        editTextDataNascimento = findViewById(R.id.btnDataNascimento);
        editTextDataNascimento.setOnClickListener(this);
        spinnerCidades = findViewById(R.id.btnCidade);
        spinnerCidades.setTitle("Selecione a cidade");
        spinnerCidades.setPositiveButton("OK");
        spinnerEstados = findViewById(R.id.btnUf);
        editTextPassword = findViewById(R.id.senhaCadastro);
        progressBar = findViewById(R.id.progressBarRegister);
        editTextDdd = findViewById(R.id.dddReg);
        editTextDdd.setFilters(new InputFilter[]{new InputFilterMinMax("0", "99")});
        editTextNumCelular = findViewById(R.id.numCelularReg);

        date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        ibgeEstados = buscasSegundoPlano("estado");

        //region Estados
        Gson jsonEstados = new GsonBuilder().setPrettyPrinting().create();
        Estado[] estados = jsonEstados.fromJson(String.valueOf(ibgeEstados), Estado[].class);

        for(Estado estado: estados){
            estadosSpinner.add(estado.getSigla());
        }
        estadosSpinner.add(0,"UF");

        ArrayAdapter<String> adapterEstados = new ArrayAdapter<String>(RegisterUser.this,
                R.layout.spinner_style,
                estadosSpinner){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the second item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the first item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerEstados.setAdapter(adapterEstados);

        spinnerEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                solicitaCidades(estadosSpinner.get(i));
                ufReg = spinnerEstados.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //endregion
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logoRegister:
                startActivity(new Intent(RegisterUser.this, MainActivity.class));
                break;
            case R.id.btnRegister:
                registerUser();
                break;
            case R.id.btnDataNascimento:
                new DatePickerDialog(RegisterUser.this, R.style.my_dialog_theme ,date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }

    private void registerUser() {
        updateLabel();
        String email = editTextEmail.getText().toString().trim();
        String nomeCompleto = editTextFullName.getText().toString().trim();
        String cpf = editTextCPF.getText().toString().trim();
        String senha = editTextPassword.getText().toString().trim();
        String dataNascimento = dataN.toString().trim();
        String cidade = cidadeReg;
        String uf = ufReg;
        String ddd = editTextDdd.getText().toString().trim();
        String numCelular = editTextNumCelular.getText().toString().trim();


        if(nomeCompleto.isEmpty()){
            editTextFullName.setError("Campo obrigatório !!");
            editTextFullName.requestFocus();
            return;
        }

        if(cpf.isEmpty()){
            editTextCPF.setError("Campo obrigatório !!");
            editTextCPF.requestFocus();
            return;
        }
        
        validateCPF();

        if(email.isEmpty()){
            editTextEmail.setError("Campo obrigatório !!");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Por favor digite um email válido!");
            editTextEmail.requestFocus();
            return;
        }

        if(ufReg.equals("UF") || ufReg == null){
            ((TextView)spinnerEstados.getSelectedView()).setError("Error message");
            return;
        }

        if(cidadeReg.equals("Cidade") || cidadeReg == null){
            ((TextView)spinnerCidades.getSelectedView()).setError("Error message");
            return;
        }

        if(senha.isEmpty()){
            editTextPassword.setError("Campo obrigatório !!");
            editTextPassword.requestFocus();
            return;
        }

        if(senha.length() < 6){
            editTextPassword.setError("Digite uma senha com no minimo 6 caracteres.");
            editTextPassword.requestFocus();
            return;
        }

        if(dataNascimento.equals(dataAtual)){
            Toast.makeText(RegisterUser.this,"Você não nasceu hoje porra",Toast.LENGTH_LONG).show();
            editTextDataNascimento.setError("Campo obrigatório !!");
            editTextDataNascimento.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(nomeCompleto, cpf , email, cidade, uf, dataNascimento, ddd, numCelular);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this, "Usuário cadastrado com sucesso !", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(RegisterUser.this, Login.class));
                                        RegisterUser.this.finish();
                                    }else{
                                        if(task.getException().toString().equals("ERROR_EMAIL_ALREADY_IN_USE")){
                                            Log.e("Warning_Activity","Else task.excpetiuon -> " + task.getException().toString());
                                            Toast.makeText(RegisterUser.this, "Este email já está sendo utilizado !", Toast.LENGTH_LONG).show();
                                        }
                                        Toast.makeText(RegisterUser.this, "Falha ao cadastrar usuário ! Tente novamente !", Toast.LENGTH_LONG).show();
                                        Log.e("Warning_Activity","Else task.isSucesseful -> Erro ao Registrar");
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        else {
                            if(task.getException().toString().equals("ERROR_EMAIL_ALREADY_IN_USE")){
                                Log.e("Warning_Activity","Else task.excpetiuon -> " + task.getException());
                                Toast.makeText(RegisterUser.this, "Este email já está sendo utilizado !", Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(RegisterUser.this, "Falha ao cadastrar usuário ! Tente novamente !", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

    private void validateCPF() {
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dataN = sdf.format(myCalendar.getTime());
        dataAtual = sdf.format(Calendar.getInstance().getTime());
        if(!dataN.toString().equals(dataAtual)){
            editTextDataNascimento.setText(dataN);
        }
    }

    private String buscasSegundoPlano(String ... params) {

        String respostaIbge = null;
        SegundoPlano segundoPlano = new SegundoPlano();
        try {
            respostaIbge = segundoPlano.execute(params).get();
        }catch (ExecutionException | InterruptedException e){
            Log.d("onPost", "Erro resposta IBGE: " + e);
        }

        return respostaIbge;
    }

    private void solicitaCidades(String siglaEstado) {

        String ibgeCidades = buscasSegundoPlano("cidade", siglaEstado);

        Gson jsonCidades = new GsonBuilder().setPrettyPrinting().create();
        Cidade[] cidades = jsonCidades.fromJson(String.valueOf(ibgeCidades), Cidade[].class);

        ArrayList<String> cidadesSpinner = new ArrayList<>();

        for(Cidade cidade: cidades){
            cidadesSpinner.add(cidade.getNome());
        }

        cidadesSpinner.add(0,"Cidade");

        ArrayAdapter<String> adapterCidades = new ArrayAdapter<String>(RegisterUser.this,
                R.layout.spinner_style,
                cidadesSpinner){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerCidades.setAdapter(adapterCidades);

        spinnerCidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cidadeReg = spinnerCidades.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        progressBar.setVisibility(View.INVISIBLE);

    }

}