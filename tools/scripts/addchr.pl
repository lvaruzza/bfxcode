#!/usr/bin/env perl

sub addchr {
    my $line=shift;
    if($line =~ /^(\d+|X|Y)/) {
	$line="chr$line";
    }elsif($line =~ /^MT/) {
	$line =~ s/(^MT)/chrM/;
    }
    return $line;
}

while(<>) {
    if(/^#/) {
	if (/##contig=<ID=(.*?),(.*)/) {
#	    print "1=$1 2=$2\n";
	    print "##contig=<ID=",addchr($1),",$2\n";
	} else {
	    print;
	}
    } else {
	print addchr($_);
    }
}
