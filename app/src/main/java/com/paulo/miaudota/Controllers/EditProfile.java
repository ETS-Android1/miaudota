package com.paulo.miaudota.Controllers;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paulo.miaudota.Utils.InputFilterMinMax;
import com.paulo.miaudota.Models.Cidade;
import com.paulo.miaudota.Models.Estado;
import com.paulo.miaudota.R;
import com.paulo.miaudota.Models.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity implements View.OnClickListener  {

    private EditText editTextFullName,editTextCPF,editTextEmail,editTextDataNascimento, editTextDdd, editTextCelular;
    private CircleImageView profilePicture;
    private ProgressBar progressBar;
    private String userID, profilePicUrl, dataN, dataAtual, ibgeEstados, cidadePet, ufPet;
    final Calendar myCalendar = Calendar.getInstance();
    private Uri cropImageUri;
    private Bitmap bitmap = null;
    DatePickerDialog.OnDateSetListener date;
    private Spinner spinnerEstados;
    private SearchableSpinner spinnerCidades = null;
    private ArrayList<String> estadosSpinner = new ArrayList<>();
    private Boolean usuarioIncompleto;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;

    @Override
    protected void onStart() {
        Log.e("Warning_Activity","onStart EditProfile -> ");
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e("Warning_Activity","onStart EditProfile userNull-> ");
            startActivity(new Intent(EditProfile.this, WelcomeScreen.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Warning_Activity", "onResume -> EditProfile");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Warning_Activity","onCreate EditProfile -> ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //carregando informações do usuário
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        profilePicture =  findViewById(R.id.profilePictureEdit);
        cropImageUri = user.getPhotoUrl();
        editTextFullName = findViewById(R.id.nomeProfileEdit);
        editTextCPF = findViewById(R.id.cpfProfileEdit);
        editTextEmail = findViewById(R.id.emailProfileEdit);
        editTextDataNascimento = findViewById(R.id.btnDataNascimentoProfileEdit);
        editTextDataNascimento.setOnClickListener(this);
        editTextDdd = findViewById(R.id.dddProfileEdit);
        editTextDdd.setFilters(new InputFilter[]{new InputFilterMinMax("0", "99")});
        editTextCelular = findViewById(R.id.numCelularProfileEdit);
        progressBar = findViewById(R.id.progressBarPRofileEdit);
        progressBar.setVisibility(View.VISIBLE);
        spinnerEstados = findViewById(R.id.ufProfileEdit);
        spinnerCidades = findViewById(R.id.cidadeProfileEdit);
        spinnerCidades.setTitle("Selecione a cidade");
        spinnerCidades.setPositiveButton("OK");

        if (user == null) {
            Log.e("Warning_Activity","onCreate EditProfile userNull-> ");
            startActivity(new Intent(EditProfile.this, WelcomeScreen.class));
        }

        ibgeEstados = buscasSegundoPlano("estado");

        //region Estados
        Gson jsonEstados = new GsonBuilder().setPrettyPrinting().create();
        Estado[] estados = jsonEstados.fromJson(String.valueOf(ibgeEstados), Estado[].class);

        for(Estado estado: estados){
            estadosSpinner.add(estado.getSigla());
        }
        estadosSpinner.add(0,"UF");

        ArrayAdapter<String> adapterEstados = new ArrayAdapter<String>(EditProfile.this,
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
                ufPet = spinnerEstados.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //endregion


        Button updateProfile = findViewById(R.id.btnUpdate);
        updateProfile.setOnClickListener(this);

        ImageButton updateProfilePicture = findViewById(R.id.btnProfilePictureEdit);
        updateProfilePicture.setOnClickListener(this);
        profilePicture.setOnClickListener(this);

        date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        //Carregar informações do usuário completo
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User userProfile = snapshot.getValue(User.class);
                String nomeCompleto;
                String email;

                try {
                    profilePicUrl = user.getPhotoUrl().toString();
                }
                catch (Exception ex){
                    Log.e("Warning_Activity","profilePicUrl -> Erro ao buscar foto: " + ex);
                }

                if(userProfile.Cpf != null){
                    //se for completo carrega tudo
                    nomeCompleto = userProfile.NomeCompleto;
                    email = userProfile.Email;
                    String cpf = userProfile.Cpf;
                    String cidade = userProfile.Cidade;
                    String uf = userProfile.UF;
                    String dataNascimento = userProfile.DataNascimento;
                    String ddd = userProfile.Ddd;
                    String nCelular = userProfile.NumCelular;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dataFormatada = null;
                    try {
                        dataFormatada = dateFormat.parse(dataNascimento);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    myCalendar.setTime(dataFormatada);

                    editTextFullName.setText(nomeCompleto);
                    editTextEmail.setText(email);
                    editTextCPF.setText(cpf);
                    spinnerEstados.setSelection(((ArrayAdapter<String>) spinnerEstados.getAdapter()).getPosition(uf));
                    solicitaCidades(uf);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            spinnerCidades.setSelection(((ArrayAdapter<String>) spinnerCidades.getAdapter()).getPosition(cidade));
                        }
                    }, 3000);

                    editTextDataNascimento.setText(dataNascimento);
                    editTextDdd.setText(ddd);
                    editTextCelular.setText(nCelular);

                    Glide.with(EditProfile.this)
                            .load(profilePicUrl) // image url
                            .placeholder(R.drawable.profile_placeholder) // any placeholder to load at start
                            .error(R.drawable.profile_placeholder)  // any image in case of error
                            .override(200, 200) // resizing
                            .centerCrop()
                            .into(profilePicture);  // imageview object

                    usuarioIncompleto = false;
                }
                else{
                    //se for incompleto carrega email e nome e a foto
                    usuarioIncompleto = true;
                    nomeCompleto = userProfile.NomeCompleto;
                    email = userProfile.Email;

                    editTextFullName.setText(nomeCompleto);
                    editTextEmail.setText(email);

                    Glide.with(EditProfile.this)
                            .load(profilePicUrl) // image url
                            .placeholder(R.drawable.profile_placeholder) // any placeholder to load at start
                            .error(R.drawable.profile_placeholder)  // any image in case of error
                            .override(200, 200) // resizing
                            .centerCrop()
                            .into(profilePicture);  // imageview object
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Warning_Activity","loadUser -> erro onCancelled.");
                Toast.makeText(EditProfile.this,"Algo deu errado !",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void solicitaCidades(String siglaEstado) {

        String ibgeCidades = buscasSegundoPlano("cidade", siglaEstado);

        Gson jsonCidades = new GsonBuilder().setPrettyPrinting().create();
        Cidade[] cidades = jsonCidades.fromJson(String.valueOf(ibgeCidades), Cidade[].class);

        ArrayList<String> cidadesSpinner = new ArrayList<>();

        String regex = "(?<!^)([A-Z])";
        for(Cidade cidade: cidades){
            cidadesSpinner.add(cidade.getNome().replaceAll(regex, " $1"));
        }

        cidadesSpinner.add(0,"Cidade");

        ArrayAdapter<String> adapterCidades = new ArrayAdapter<String>(EditProfile.this,
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
                cidadePet = spinnerCidades.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnUpdate:
                updateProfile();
                break;
            case R.id.btnDataNascimentoProfileEdit:
                new DatePickerDialog(EditProfile.this, R.style.my_dialog_theme ,date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.btnProfilePictureEdit:
            case R.id.profilePictureEdit:
                updateProfilePicture();
                break;
        }
    }

    ActivityResultLauncher<Intent> profilePicActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();
                        cropImageUri = data.getData();

                        CropImage.activity(cropImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropMenuCropButtonTitle("Meter o corte")
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1,1)
                        .start(EditProfile.this);
                    }
                }
    });

    public void updateProfilePicture() {
        Intent openGalleryIntent = new Intent();
        openGalleryIntent.setType("image/*");
        openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        profilePicActivityResultLauncher.launch(openGalleryIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                cropImageUri = result.getUri();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(cropImageUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
                catch (Exception ex){
                    Log.e("TAG","falhou bitmap: ", ex);
                }
                Glide.with(EditProfile.this)
                        .load(cropImageUri.toString()) // image url
                        .placeholder(R.drawable.profile_placeholder) // any placeholder to load at start
                        .error(R.drawable.profile_placeholder)  // any image in case of error
                        .override(200, 200) // resizing
                        .centerCrop()
                        .into(profilePicture);  // imageview object
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("Warning_Activity","profilePicUrl -> Erro ao cortar foto: " + error);
                Glide.with(EditProfile.this)
                        .load(profilePicUrl) // image url
                        .placeholder(R.drawable.profile_placeholder) // any placeholder to load at start
                        .error(R.drawable.profile_placeholder)  // any image in case of error
                        .override(200, 200) // resizing
                        .centerCrop()
                        .into(profilePicture);  // imageview object
            }
        }
    }

    private void updateProfile() {
        updateLabel();

        String email = editTextEmail.getText().toString().trim();
        String nomeCompleto = editTextFullName.getText().toString().trim();
        String cpf = editTextCPF.getText().toString().trim();
        String cidade = cidadePet;
        String uf = ufPet;
        String dataNascimento = dataN.toString().trim();
        String ddd = editTextDdd.getText().toString().trim();
        String numCelular = editTextCelular.getText().toString().trim();

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

        if(validateCPF(cpf) == false){
            editTextCPF.setError("CPF inválido !!");
            editTextCPF.requestFocus();
            return;
        }
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

        if(ufPet.equals("UF") || ufPet == null){
            ((TextView)spinnerEstados.getSelectedView()).setError("Error message");
            return;
        }

        if(cidadePet.equals("Cidade") || cidadePet == null){
            ((TextView)spinnerCidades.getSelectedView()).setError("Error message");
            return;
        }

        if(dataNascimento.equals(dataAtual)){
            Toast.makeText(EditProfile.this,"Você não nasceu hoje porra",Toast.LENGTH_SHORT).show();
            editTextDataNascimento.setError("Campo obrigatório !!");
            editTextDataNascimento.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        User usuario = new User(nomeCompleto, cpf , email, cidade, uf, dataNascimento, ddd, numCelular);
        reference.child(userID).setValue(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if(bitmap != null){
                    handleUpload(bitmap);
                }
                else{
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Intent intent = new Intent(EditProfile.this,Home.class);
                            startActivity(intent);
                            Toast.makeText(EditProfile.this,"Perfil atualizado com sucesso !!",Toast.LENGTH_SHORT).show();
                        }
                    }, 3500);
                }
            }
        });
    }

    private void handleUpload(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(userID + ".jpeg");

        ref.putBytes(baos.toByteArray())
                .addOnSuccessListener(taskSnapshot -> getDownloadUrl(ref))
                .addOnFailureListener(e -> Log.e("TAG","onFailure: ", e.getCause() ));
    }

    private void getDownloadUrl(StorageReference sReference){
        sReference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.e("TAG","onSuccess: " + uri );
                    setUserProfileUrl(uri);
                });
    }

    private void setUserProfileUrl(Uri uri){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(request)
                .addOnSuccessListener(unused -> {
                    Log.d("Profile_Photo", "User profile updated.");
                    Intent intent = new Intent(EditProfile.this,Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); finish();
                    startActivity(intent);
                    Toast.makeText(EditProfile.this,"Perfil atualizado com sucesso !!",Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("TAG","onFailure: ", e.getCause() );
                    Toast.makeText(EditProfile.this,"Falha ao enviar imagem do perfil...",Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateCPF(String CPF) {

        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF.equals("00000000000") ||
                CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11))
            return(false);

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i=0; i<9; i++) {
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char)(r + 48); // converte no respectivo caractere numerico

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for(i=0; i<10; i++) {
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else dig11 = (char)(r + 48);

            // Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
                return(true);
            else return(false);
        } catch (Exception erro) {
            return(false);
        }
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

    @Override
    public void onBackPressed(){
        Log.e("Warning_Activity","onBackPressed Home");

        if(usuarioIncompleto == true){
            startActivity(new Intent(EditProfile.this, Home.class));
        }
        else {
            super.onBackPressed();
        }
    }

}