<?php




if( !isset($argv) || count($argv)<=1 ) {
    phpDie("Usage: php ".__FILE__." /path/to/resources/l10n");
    exit(1);
}

$resourceDir = $argv[1];

if( !is_dir($resourceDir) ) {
    phpDie( $resourceDir." is not a dir" );
}

if( !file_exists( $resourceDir."/labels.properties")) {
    phpDie( $resourceDir." is missing default labels.properties file" );
}

$defaultLang = parse_ini_file($resourceDir."/labels.properties", false, INI_SCANNER_RAW);

//print_r($defaultLang);

if( empty($defaultLang)) {
    phpDie( "labels.properties is invalid or empty" );
}

$langFiles = glob($resourceDir."/labels_*");

$errors = 0;

foreach( $langFiles as $langfile ) {
    $checkLang = parse_ini_file($langfile, false, INI_SCANNER_RAW);
    if( empty($checkLang)) {
        echo $langfile." is invalid or empty".PHP_EOL;
        $errors++;

    }
    foreach( $defaultLang as $l10nKey => $l10nval ) {
        if( !isset($checkLang[$l10nKey]) || empty($checkLang[$l10nKey]) ) {
            echo basename($langfile)." is missing translation for key ".$l10nKey.PHP_EOL;
            $errors++;
        }
    }
}

if( $errors > 0 ) {
    phpDie("l10n check failed with ".$errors." errors");
}


function phpDie($msg) {
    echo $msg.PHP_EOL;
    exit(1);
}
