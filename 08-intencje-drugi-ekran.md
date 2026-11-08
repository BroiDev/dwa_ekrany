# Drugi ekran: intencje i przekazywanie danych

**Technik programista | klasa 5 | aplikacje mobilne**

Activity i stos ekranów, intencja jawna, `putExtra`, powrót z wynikiem,
intencje niejawne i otwieranie innych aplikacji.

---

## 1. Punkt wyjścia

Formularz z poprzednich zajęć kończył się tak: użytkownik klikał „wyślij",
a podsumowanie pojawiało się w etykiecie **pod przyciskiem, na tym samym ekranie**.

Żadna prawdziwa aplikacja tak nie robi. Po wypełnieniu formularza przechodzisz
na ekran podsumowania, sprawdzasz dane, potwierdzasz albo wracasz do poprawek.
Dwa ekrany, przejście w jedną stronę i powrót z odpowiedzią.

To jest treść tej lekcji – i pierwsza, w której Twoja aplikacja przestaje być
jednym ekranem.

---

## 2. Activity i stos ekranów

**Jeden ekran to jedna aktywność.** Każda ma trzy części, o których już wiesz
z pierwszego projektu:

| Część | Przykład |
|---|---|
| Klasa Javy | `SummaryActivity.java` |
| Plik układu | `activity_summary.xml` |
| Wpis w manifeście | `<activity android:name=".SummaryActivity" />` |

Android układa otwarte ekrany na **stosie**. Nowy ekran ląduje na wierzchu
i przykrywa poprzedni. Przycisk Wstecz zdejmuje wierzchni ekran i odsłania ten pod nim.

```
   start              po przejściu         po Wstecz
   ┌──────────┐       ┌──────────┐         ┌──────────┐
   │          │       │ Summary  │ ← widok │          │
   │   Main   │ ←widok├──────────┤         │   Main   │ ← widok
   │          │       │   Main   │         │          │
   └──────────┘       └──────────┘         └──────────┘
```

Stos to zwykła struktura „ostatni wchodzi, pierwszy wychodzi" – ta sama,
którą znasz z programowania. Tutaj jest tylko widoczna gołym okiem: **przycisk
Wstecz to operacja zdjęcia ze stosu.**

Metoda `finish()` zamyka bieżący ekran i zdejmuje go ze stosu z poziomu kodu –
działa dokładnie tak jak przycisk Wstecz.

> Gdy stos jest pusty, aplikacja się kończy. Dlatego Wstecz na pierwszym ekranie
> wychodzi z aplikacji, a nie prowadzi donikąd.

---

## 3. Tworzenie drugiego ekranu

**Prawy przycisk na pakiecie → New → Activity → Empty Views Activity.**

Android Studio tworzy trzy rzeczy naraz:

1. klasę `SummaryActivity.java`,
2. układ `res/layout/activity_summary.xml`,
3. **wpis w `AndroidManifest.xml`** – i to jest najważniejsze.

```xml
<activity
    android:name=".SummaryActivity"
    android:exported="false"
    android:parentActivityName=".MainActivity" />
```

| Atrybut | Znaczenie |
|---|---|
| `android:name=".SummaryActivity"` | nazwa klasy; kropka na początku to skrót od nazwy pakietu |
| `android:exported="false"` | ekran dostępny **tylko dla Twojej aplikacji** |
| `android:parentActivityName` | ekran nadrzędny – daje strzałkę powrotu na pasku |

> **Ekran bez wpisu w manifeście nie istnieje.** Projekt się skompiluje,
> aplikacja się uruchomi, a przy próbie przejścia dostaniesz
> `ActivityNotFoundException`. Dzieje się tak zawsze, gdy klasę tworzysz ręcznie
> albo gdy wygeneruje ją agent – kreator Android Studio dopisuje wpis sam,
> agent zwykle nie.

**`android:exported`** jest obowiązkowy dla każdego ekranu z filtrem intencji
od Androida 12. Ekran startowy ma `true`, bo uruchamia go system po dotknięciu
ikony. Wszystkie pozostałe mają `false`.

---

## 4. Intencja jawna – przejście na drugi ekran

**Intencja** to obiekt opisujący zamiar: „chcę otworzyć ten ekran",
„chcę wysłać ten tekst", „chcę pokazać tę stronę". Jest jawna, gdy wprost wskazuje
klasę do uruchomienia.

```java
Intent intent = new Intent(this, SummaryActivity.class);
startActivity(intent);
```

Dwa argumenty: **kontekst** (`this`, czyli bieżąca aktywność) i **klasa ekranu
docelowego**. Zapis `SummaryActivity.class` to nie obiekt ekranu – to informacja
o typie. Android sam utworzy aktywność we właściwym momencie; nigdy nie piszesz
`new SummaryActivity()`.

> **Aktywności nie tworzysz operatorem `new`.** Robi to system, bo musi
> przy okazji nadać jej kontekst, cykl życia i miejsce na stosie. Obiekt
> utworzony ręcznie byłby pustą skorupą bez dostępu do czegokolwiek.

---

## 5. Przekazywanie danych

Intencja oprócz celu niesie **dodatki** – pary klucz-wartość.

### Nadawanie

```java
Intent intent = new Intent(this, SummaryActivity.class);
intent.putExtra(SummaryActivity.EXTRA_NAME, "Anna Nowak");
intent.putExtra(SummaryActivity.EXTRA_LEVEL, 7);
intent.putExtra(SummaryActivity.EXTRA_NEWSLETTER, true);
startActivity(intent);
```

### Odbieranie

```java
Intent intent = getIntent();
String name = intent.getStringExtra(EXTRA_NAME);
int level = intent.getIntExtra(EXTRA_LEVEL, 0);
boolean newsletter = intent.getBooleanExtra(EXTRA_NEWSLETTER, false);
```

| Typ | Zapis | Odczyt | Gdy brak klucza |
|---|---|---|---|
| `String` | `putExtra(k, s)` | `getStringExtra(k)` | `null` |
| `int` | `putExtra(k, i)` | `getIntExtra(k, 0)` | wartość domyślna |
| `double` | `putExtra(k, d)` | `getDoubleExtra(k, 0)` | wartość domyślna |
| `boolean` | `putExtra(k, b)` | `getBooleanExtra(k, false)` | wartość domyślna |
| `String[]` | `putExtra(k, t)` | `getStringArrayExtra(k)` | `null` |

Zwróć uwagę na niekonsekwencję: metody dla typów prostych **wymagają wartości
domyślnej**, a `getStringExtra` jej nie przyjmuje i po prostu zwraca `null`.

Literówka w kluczu nie jest więc błędem kompilacji, tylko cichą awarią,
i objawia się inaczej w zależności od typu. Przy `int` dostaniesz zero
i możesz tego w ogóle nie zauważyć. Przy `String` dostaniesz `null` –
który sam w sobie jeszcze nic nie psuje, ale pierwsze wywołanie na nim
czegokolwiek, choćby `trim()` albo `length()`, kończy się
`NullPointerException`.

### Klucze jako stałe

Klucz to zwykły napis, ale **nigdy nie wpisujemy go wprost w dwóch miejscach**.
Literówka w jednym z nich nie jest błędem kompilacji – jest cichą awarią.

```java
public class SummaryActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "com.example.dwaekrany.NAME";
    public static final String EXTRA_LEVEL = "com.example.dwaekrany.LEVEL";
    // ...
}
```

Stałe deklarujemy **w ekranie, który te dane odbiera** – to on definiuje,
czego oczekuje. Ekran nadający odwołuje się do nich przez nazwę klasy:
`SummaryActivity.EXTRA_NAME`. Dzięki temu literówka staje się błędem kompilacji.

Wartość klucza poprzedzamy nazwą pakietu. Dodatki trafiają do wspólnego worka,
który czasem przechodzi między aplikacjami, a `"NAME"` to napis, którego używa
pół świata. `"com.example.dwaekrany.NAME"` jest Twój i tylko Twój.

---

## 6. Powrót z wynikiem

Przejście w jedną stronę to połowa sprawy. Ekran podsumowania ma oddać odpowiedź:
potwierdzono czy nie.

### Czego nie używamy

W każdym starszym tutorialu i w kodzie, który zaproponuje agent, zobaczysz:

```java
startActivityForResult(intent, 1);      // PRZESTARZAŁE

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) { }
```

**Obie metody są oznaczone jako przestarzałe.** Nadal działają, ale nie będziemy
ich używać. To ten sam rodzaj pułapki co `switch` po `R.id` z lekcji o kalkulatorze:
kod poprawny kilka lat temu, dziś zastąpiony czymś innym.

### Nowy sposób – dwa kroki

**Krok 1. Rejestrujesz obsługę wyniku** – jako pole klasy:

```java
private final ActivityResultLauncher<Intent> summaryLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                String number = result.getData().getStringExtra(SummaryActivity.EXTRA_NUMBER);
                showRegistered(number);
            }
        });
```

**Krok 2. Uruchamiasz ekran przez tę rejestrację**, nie przez `startActivity`:

```java
summaryLauncher.launch(intent);
```

> **Rejestracja musi się odbyć zanim ekran stanie się widoczny** – czyli w polu
> klasy albo na początku `onCreate`. Wywołanie `registerForActivityResult`
> wewnątrz obsługi kliknięcia kończy się wyjątkiem z komunikatem
> *„LifecycleOwners must call register before they are STARTED"*.
>
> Powód jest praktyczny: gdy system zniszczy i odtworzy Twój ekran, rejestracja
> musi istnieć, **zanim** przyjdzie wynik z poprzednio otwartego ekranu.
> Rejestracja w polu klasy spełnia ten warunek zawsze.

### Zwracanie wyniku z drugiego ekranu

```java
private void confirm() {
    Intent result = new Intent();
    result.putExtra(EXTRA_NUMBER, "ZG/4821");
    setResult(RESULT_OK, result);
    finish();
}
```

`setResult` ustawia odpowiedź, `finish()` zamyka ekran i dopiero wtedy wynik
wraca do ekranu wywołującego. Samo `setResult` bez `finish()` nie robi nic widocznego.

| Stała | Kiedy |
|---|---|
| `RESULT_OK` | użytkownik potwierdził |
| `RESULT_CANCELED` | zrezygnował – **ustawiana automatycznie** |

Jeśli użytkownik naciśnie Wstecz albo wywołasz samo `finish()` bez `setResult`,
ekran wywołujący i tak dostanie `RESULT_CANCELED`. Nie musisz tego obsługiwać ręcznie.

---

## 7. Intencje niejawne – inne aplikacje

Intencja jawna wskazuje klasę. **Intencja niejawna opisuje czynność**, a system
szuka aplikacji, która potrafi ją wykonać.

```java
Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://zsejg.edu.pl"));
startActivity(intent);
```

Nie wskazujesz przeglądarki. Mówisz „pokaż to", a system sam wybiera aplikację –
albo pyta użytkownika, jeśli pasuje kilka.

| Czynność | Kod |
|---|---|
| Strona internetowa | `new Intent(ACTION_VIEW, Uri.parse("https://..."))` |
| Numer w dialerze | `new Intent(ACTION_DIAL, Uri.parse("tel:123456789"))` |
| Nowy e-mail | `new Intent(ACTION_SENDTO, Uri.parse("mailto:a@b.pl"))` |
| Mapa | `new Intent(ACTION_VIEW, Uri.parse("geo:50.9,15.7?q=Jelenia+Góra"))` |
| Udostępnienie tekstu | `ACTION_SEND` z typem `text/plain` |

### Udostępnianie i okno wyboru

```java
Intent intent = new Intent(Intent.ACTION_SEND);
intent.setType("text/plain");
intent.putExtra(Intent.EXTRA_TEXT, summary);
startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
```

`createChooser` wymusza okno wyboru aplikacji, nawet jeśli użytkownik ustawił
wcześniej domyślną. Przy udostępnianiu to jest zachowanie pożądane.

Zwróć uwagę, że `Intent.EXTRA_TEXT` to **gotowa stała systemowa** – odbiorcą
jest cudza aplikacja, więc klucz musi być z góry ustalony. Tylko do własnych
ekranów definiujesz własne klucze.

### Gdy nie ma czym otworzyć

Na emulatorze bez przeglądarki czy klienta poczty `startActivity` rzuca wyjątek
i aplikacja gaśnie. Zabezpieczenie to zwykły blok `try`:

```java
try {
    startActivity(intent);
} catch (ActivityNotFoundException e) {
    Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show();
}
```

> **Nie używaj do tego `resolveActivity`.** Starsze materiały radzą najpierw
> sprawdzić `intent.resolveActivity(getPackageManager()) != null`. Od Androida 11
> aplikacja domyślnie **nie widzi** większości innych aplikacji, więc ta metoda
> zwraca `null` nawet wtedy, gdy `startActivity` zadziałałoby bez problemu.
> Efekt: Twój kod uznaje, że nie ma przeglądarki, choć jest.
> Blok `try` jest prostszy i działa poprawnie na każdej wersji systemu.

---

## 8. Kompletny przykład – dwa ekrany

Formularz zapisu, ekran podsumowania, potwierdzenie z numerem zgłoszenia
i udostępnianie. Walidację pomijam, bo była na poprzednich zajęciach –
w swoim projekcie zostaje.

### `AndroidManifest.xml` – fragment

```xml
<!-- wewnątrz znacznika <application>, atrybuty szablonu zostają bez zmian -->

<activity
    android:name=".MainActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<activity
    android:name=".SummaryActivity"
    android:exported="false"
    android:parentActivityName=".MainActivity" />
```

### `res/values/strings.xml`

```xml
<resources>
    <string name="app_name">Dwa ekrany</string>

    <string name="header_form">Zapisy na kurs</string>
    <string name="label_name">Imię i nazwisko</string>
    <string name="label_email">Adres e-mail</string>
    <string name="label_course">Kurs</string>
    <string name="label_level">Poziom zaawansowania</string>
    <string name="newsletter">Chcę dostawać newsletter</string>
    <string name="btn_next">DALEJ</string>

    <string name="header_summary">Sprawdź dane</string>
    <string name="btn_confirm">POTWIERDŹ</string>
    <string name="btn_edit">POPRAW DANE</string>
    <string name="btn_share">Udostępnij</string>

    <string name="summary_format">Imię: %1$s\nE-mail: %2$s\nKurs: %3$s\nPoziom: %4$d/10\nNewsletter: %5$s</string>
    <string name="yes_short">tak</string>
    <string name="no_short">nie</string>

    <string name="registered">Zapisano, numer zgłoszenia %1$s</string>
    <string name="share_via">Udostępnij przez</string>
    <string name="error_no_app">Brak aplikacji do obsługi tej czynności</string>

    <string-array name="courses">
        <item>Programowanie w Javie</item>
        <item>Aplikacje mobilne</item>
        <item>Bazy danych</item>
    </string-array>
</resources>
```

### `res/layout/activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/tvHeader"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#008080"
        android:padding="12dp"
        android:text="@string/header_form"
        android:textColor="#FFFFFF"
        android:textSize="24sp" />

    <TextView
        android:id="@+id/tvLabelName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="@string/label_name" />

    <EditText
        android:id="@+id/etName"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPersonName"
        android:maxLines="1" />

    <TextView
        android:id="@+id/tvLabelEmail"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:text="@string/label_email" />

    <EditText
        android:id="@+id/etEmail"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textEmailAddress"
        android:maxLines="1" />

    <TextView
        android:id="@+id/tvLabelCourse"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:text="@string/label_course" />

    <Spinner
        android:id="@+id/spCourse"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

    <LinearLayout
        android:id="@+id/rowLevel"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:orientation="horizontal">

        <TextView
            android:id="@+id/tvLabelLevel"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="@string/label_level" />

        <TextView
            android:id="@+id/tvLevelValue"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textStyle="bold"
            tools:text="5" />
    </LinearLayout>

    <SeekBar
        android:id="@+id/sbLevel"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:max="10"
        android:progress="0" />

    <CheckBox
        android:id="@+id/cbNewsletter"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:text="@string/newsletter" />

    <Button
        android:id="@+id/btnNext"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:text="@string/btn_next" />

</LinearLayout>
```

### `res/layout/activity_summary.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/summary"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    tools:context=".SummaryActivity">

    <TextView
        android:id="@+id/tvHeaderSummary"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#008080"
        android:padding="12dp"
        android:text="@string/header_summary"
        android:textColor="#FFFFFF"
        android:textSize="24sp" />

    <TextView
        android:id="@+id/tvSummary"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:lineSpacingExtra="4dp"
        android:textSize="18sp"
        tools:text="Imię: Anna Nowak" />

    <Button
        android:id="@+id/btnShare"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="@string/btn_share" />

    <LinearLayout
        android:id="@+id/rowButtons"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="32dp"
        android:orientation="horizontal">

        <Button
            android:id="@+id/btnEdit"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginEnd="8dp"
            android:layout_weight="1"
            android:text="@string/btn_edit" />

        <Button
            android:id="@+id/btnConfirm"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="@string/btn_confirm" />
    </LinearLayout>

</LinearLayout>
```

### `MainActivity.java`

```java
package com.example.dwaekrany;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    // rejestracja MUSI powstać zanim ekran stanie się widoczny - stąd pole klasy
    private final ActivityResultLauncher<Intent> summaryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String number = result.getData()
                            .getStringExtra(SummaryActivity.EXTRA_NUMBER);
                    showRegistered(number);
                }
                // RESULT_CANCELED - użytkownik wrócił do poprawek, nic nie robimy
            });

    private EditText etName, etEmail;
    private Spinner spCourse;
    private SeekBar sbLevel;
    private TextView tvLevelValue;
    private CheckBox cbNewsletter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        spCourse = findViewById(R.id.spCourse);
        sbLevel = findViewById(R.id.sbLevel);
        tvLevelValue = findViewById(R.id.tvLevelValue);
        cbNewsletter = findViewById(R.id.cbNewsletter);

        setUpCourseSpinner();
        setUpLevelSeekBar();

        findViewById(R.id.btnNext).setOnClickListener(v -> openSummary());
    }

    private void setUpCourseSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.courses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCourse.setAdapter(adapter);
    }

    private void setUpLevelSeekBar() {
        tvLevelValue.setText(String.valueOf(sbLevel.getProgress()));
        sbLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvLevelValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void openSummary() {
        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra(SummaryActivity.EXTRA_NAME, etName.getText().toString().trim());
        intent.putExtra(SummaryActivity.EXTRA_EMAIL, etEmail.getText().toString().trim());
        intent.putExtra(SummaryActivity.EXTRA_COURSE, spCourse.getSelectedItem().toString());
        intent.putExtra(SummaryActivity.EXTRA_LEVEL, sbLevel.getProgress());
        intent.putExtra(SummaryActivity.EXTRA_NEWSLETTER, cbNewsletter.isChecked());

        summaryLauncher.launch(intent);        // zamiast startActivity
    }

    private void showRegistered(String number) {
        Snackbar.make(findViewById(R.id.main),
                getString(R.string.registered, number),
                Snackbar.LENGTH_LONG).show();
    }
}
```

### `SummaryActivity.java`

```java
package com.example.dwaekrany;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class SummaryActivity extends AppCompatActivity {

    // klucze definiuje ekran, który dane ODBIERA
    public static final String EXTRA_NAME = "com.example.dwaekrany.NAME";
    public static final String EXTRA_EMAIL = "com.example.dwaekrany.EMAIL";
    public static final String EXTRA_COURSE = "com.example.dwaekrany.COURSE";
    public static final String EXTRA_LEVEL = "com.example.dwaekrany.LEVEL";
    public static final String EXTRA_NEWSLETTER = "com.example.dwaekrany.NEWSLETTER";
    public static final String EXTRA_NUMBER = "com.example.dwaekrany.NUMBER";

    private TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        tvSummary = findViewById(R.id.tvSummary);
        tvSummary.setText(buildSummary());

        findViewById(R.id.btnConfirm).setOnClickListener(v -> confirm());
        findViewById(R.id.btnEdit).setOnClickListener(v -> finish());
        findViewById(R.id.btnShare).setOnClickListener(v -> share());
    }

    private String buildSummary() {
        Intent intent = getIntent();

        String name = intent.getStringExtra(EXTRA_NAME);
        String email = intent.getStringExtra(EXTRA_EMAIL);
        String course = intent.getStringExtra(EXTRA_COURSE);
        int level = intent.getIntExtra(EXTRA_LEVEL, 0);
        boolean newsletter = intent.getBooleanExtra(EXTRA_NEWSLETTER, false);

        String newsletterText = newsletter
                ? getString(R.string.yes_short)
                : getString(R.string.no_short);

        return getString(R.string.summary_format, name, email, course, level, newsletterText);
    }

    private void confirm() {
        String number = "ZG/" + (1000 + new Random().nextInt(9000));

        Intent result = new Intent();
        result.putExtra(EXTRA_NUMBER, number);
        setResult(RESULT_OK, result);
        finish();                      // dopiero finish odsyła wynik
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, tvSummary.getText().toString());

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show();
        }
    }
}
```

Przycisk „Popraw dane" wywołuje samo `finish()` – bez `setResult`. Ekran
wywołujący dostanie wtedy `RESULT_CANCELED` i nic nie zrobi. Dokładnie ten sam
efekt daje przycisk Wstecz, więc oba zachowują się tak samo i nie trzeba
niczego dopisywać.

---

## 9. Przekazywanie całych obiektów

Pięć dodatków to jeszcze da się znieść. Przy piętnastu zaczyna to wyglądać źle
i pojawia się naturalne pytanie: czy nie da się przekazać całego obiektu?

Da się – klasa musi zadeklarować, że potrafi się zapisać:

```java
public class Registration implements java.io.Serializable {
    public String name;
    public String email;
    public int level;
}
```

```java
intent.putExtra(EXTRA_DATA, registration);                              // nadanie
Registration r = (Registration) getIntent().getSerializableExtra(EXTRA_DATA);   // odbiór
```

Dwie uwagi, zanim tego użyjesz:

- Android Studio podkreśli `getSerializableExtra` na żółto. Od Androida 13
  istnieje bezpieczniejsza wersja przyjmująca dodatkowo klasę obiektu,
  ale nasz projekt obsługuje też starsze systemy, więc zostajemy przy tej.
  Ostrzeżenie nie jest błędem.
- `Serializable` jest wygodny, ale wolny. Android ma własny, szybszy mechanizm
  o nazwie `Parcelable` – wymaga więcej pisania i wrócimy do niego przy listach.

Na tej lekcji przekazujemy pojedyncze dodatki. Obiekty – gdy będzie ich czym napełnić.

---

## 10. Co się dzieje z pierwszym ekranem

Gdy otwierasz drugi ekran, pierwszy **nie jest niszczony** – schodzi na stos
i czeka. Jego pola klasy zachowują wartości.

| Czynność | Pierwszy ekran | Drugi ekran |
|---|---|---|
| `launch(intent)` | `onPause`, potem `onStop` | `onCreate`, `onStart`, `onResume` |
| Wstecz na drugim | `onRestart`, `onStart`, `onResume` | `onPause`, `onStop`, `onDestroy` |

Dlatego po powrocie formularz jest nadal wypełniony – nie trzeba nic odtwarzać.
Wyjątkiem jest sytuacja, w której system zabierze pamięć zatrzymanemu ekranowi.
Wtedy przy powrocie zostanie on utworzony od nowa i pola wrócą do wartości
początkowych – ten sam problem co przy obrocie ekranu w kalkulatorze.
Rozwiązaniem jest zapis stanu i zajmiemy się nim na osobnych zajęciach.

Podgląd w Logcacie ustawisz tak jak przy kalkulatorze – `Log.d` w każdej metodzie
cyklu życia obu ekranów. Warto to zobaczyć raz na własne oczy.

---

## 11. Częste błędy

| Objaw | Przyczyna | Rozwiązanie |
|---|---|---|
| `ActivityNotFoundException` przy przejściu | brak wpisu `<activity>` w manifeście | dopisz wpis albo twórz ekrany kreatorem |
| `NullPointerException` na odebranym napisie | literówka w kluczu, `getStringExtra` zwrócił `null` | klucze jako stałe `public static final` |
| Odebrana liczba zawsze zero | literówka w kluczu, zadziałała wartość domyślna | jak wyżej, błąd jest cichy |
| *LifecycleOwners must call register before they are STARTED* | `registerForActivityResult` w obsłudze kliknięcia | przenieś do pola klasy |
| Wynik nie wraca | `setResult` bez `finish()` | `finish()` odsyła wynik |
| Wynik wraca pusty | `startActivity` zamiast `launcher.launch` | uruchamiaj przez rejestrację |
| Aplikacja gaśnie przy udostępnianiu | brak aplikacji obsługującej | blok `try` z `ActivityNotFoundException` |
| „Nie ma przeglądarki", choć jest | `resolveActivity` na Androidzie 11+ | nie używaj go, zostaw `try` |
| Brak strzałki powrotu na pasku | brak `android:parentActivityName` | dopisz w manifeście |
| Drugi ekran otwiera się wielokrotnie | `launch` w metodzie wywoływanej wiele razy | sprawdź, gdzie podpinasz listener |

---

## 12. Ściąga

**Przejście i dane**

```java
Intent intent = new Intent(this, SummaryActivity.class);
intent.putExtra(SummaryActivity.EXTRA_NAME, "Anna");
startActivity(intent);                       // bez oczekiwania na wynik
```

**Odbiór**

```java
String s = getIntent().getStringExtra(EXTRA_NAME);       // null gdy brak
int i = getIntent().getIntExtra(EXTRA_LEVEL, 0);         // wartość domyślna
boolean b = getIntent().getBooleanExtra(EXTRA_FLAG, false);
```

**Z wynikiem**

```java
// pole klasy
private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> { if (result.getResultCode() == RESULT_OK) { /* ... */ } });

launcher.launch(intent);                     // uruchomienie

// na drugim ekranie
setResult(RESULT_OK, dataIntent);
finish();
```

**Intencje niejawne**

| Zapis | Efekt |
|---|---|
| `new Intent(ACTION_VIEW, Uri.parse("https://..."))` | przeglądarka |
| `new Intent(ACTION_DIAL, Uri.parse("tel:..."))` | dialer |
| `new Intent(ACTION_SENDTO, Uri.parse("mailto:..."))` | poczta |
| `ACTION_SEND` + `setType("text/plain")` + `EXTRA_TEXT` | udostępnianie |
| `Intent.createChooser(intent, tytul)` | wymuszone okno wyboru |

Każde `startActivity` z intencją niejawną owijasz w blok `try`
z `ActivityNotFoundException`.

**Manifest**

```xml
<activity android:name=".SummaryActivity"
    android:exported="false"
    android:parentActivityName=".MainActivity" />
```

---

## 13. Zadania

Jak zawsze: osobne repozytorium, `README.md` ze zrzutami **obu ekranów**,
commit po każdym kroku, teksty w `strings.xml`.

### Zadanie 1 – dwa ekrany

Zbuduj aplikację z rozdziału 8. Oddajesz w pięciu commitach:

1. `feat: drugi ekran i przejscie przez intencje jawna`
2. `feat: przekazanie danych formularza przez putExtra`
3. `feat: powrot z wynikiem przez ActivityResultLauncher`
4. `feat: udostepnianie podsumowania intencja niejawna`
5. `docs: README ze zrzutami obu ekranow`

W `README.md` odpowiedz na dwa pytania:

- **Dlaczego rejestracja wyniku jest polem klasy, a nie znajduje się w obsłudze
  kliknięcia przycisku?**
- **Co zobaczy ekran wywołujący, gdy użytkownik naciśnie na drugim ekranie
  przycisk Wstecz?** Sprawdź to w praktyce, nie zgaduj.

### Zadanie 2 – własna aplikacja dwuekranowa

Weź **swój formularz z poprzednich zajęć** i rozbij go na dwa ekrany: formularz
i podsumowanie. Jeśli Twój temat na to nie pozwala, wybierz jeden z poniższych.

| Temat | Ekran 1 | Ekran 2 |
|---|---|---|
| **Zamówienie pizzy** | rozmiar, dodatki, adres | podsumowanie z ceną, potwierdź albo popraw |
| **Rezerwacja wizyty** | usługa, data, dane kontaktowe | potwierdzenie z numerem rezerwacji |
| **Quiz** | pytanie i odpowiedzi | wynik z liczbą punktów, „zagraj ponownie" zwraca wynik |
| **Przelicznik walut** | kwota i waluta | wynik przeliczenia, „udostępnij" wysyła go dalej |
| **Zgłoszenie usterki** | kategoria, opis, priorytet | podgląd zgłoszenia, numer po potwierdzeniu |

**Wymagania:**

1. Drugi ekran utworzony kreatorem, z wpisem w manifeście i `parentActivityName`.
2. **Minimum cztery dodatki**, w tym co najmniej jeden `int` albo `boolean`.
3. Klucze jako stałe `public static final` w ekranie odbierającym.
4. Powrót z wynikiem przez `ActivityResultLauncher`, z obsługą **obu**
   przypadków: potwierdzenia i rezygnacji.
5. Jedna **intencja niejawna** – udostępnianie, e-mail, telefon albo mapa –
   zabezpieczona blokiem `try`.
6. Walidacja na pierwszym ekranie, zanim nastąpi przejście.

**O co zapytam:** czym różni się intencja jawna od niejawnej i w którym miejscu
Twojej aplikacji użyłeś której.

### Zadanie 3 – trzy ekrany i stos

Dołóż do aplikacji z zadania 2 **trzeci ekran** – ekran „O aplikacji",
otwierany z pierwszego ekranu i niczego nie zwracający.

W `docs/stos.md` opisz, co dzieje się ze stosem przy przejściu
ekran 1 → ekran 2 → Wstecz → ekran 3 → Wstecz → Wstecz. Narysuj stos
po każdym kroku. Sprawdź swoją odpowiedź na emulatorze, dopisując `Log.d`
w metodach `onCreate`, `onStop` i `onDestroy` wszystkich trzech ekranów,
i wklej fragment Logcatu.

### Zadanie 4 – `[bez AI]`

Bez agenta i bez czatu, wyłącznie ten materiał i dokumentacja.

Napisz aplikację **„Losowy wybór"**:

- ekran 1: pole tekstowe, przycisk „Dodaj" dopisujący wpis do listy pokazywanej
  w etykiecie, oraz przycisk „Losuj" przekazujący wszystkie wpisy na drugi ekran,
- ekran 2: pokazuje jeden wylosowany wpis i liczbę wszystkich wpisów,
  przycisk „Losuj ponownie" losuje na miejscu, przycisk „Wybieram to" zwraca
  wylosowany wpis na ekran pierwszy,
- ekran 1 po powrocie pokazuje wybrany wpis w `Snackbarze`.

Wpisy przekaż jako tablicę napisów – `putExtra` przyjmuje `String[]`,
a odbierasz przez `getStringArrayExtra`. Pusta lista ma być obsłużona
komunikatem, a nie awarią.

