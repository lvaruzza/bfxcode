#!/usr/bin/env perl

use Bio::SeqIO;

$in  = Bio::SeqIO->new(-file => $ARGV[0] , '-format' => 'Fastq');

while ( my $seq = $in->next_seq() ) {
    print ">",$seq->id(),"\n";
    print $seq->qual_text(),"\n";
}
