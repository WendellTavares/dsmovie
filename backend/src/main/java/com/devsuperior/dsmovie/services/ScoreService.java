package com.devsuperior.dsmovie.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.Movie;
import com.devsuperior.dsmovie.entities.Score;
import com.devsuperior.dsmovie.entities.User;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.repositories.UserRepository;

@Service
public class ScoreService {

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ScoreRepository scoreRepository;

	@Transactional
	public MovieDTO saveScore(ScoreDTO dto) {
		
		//Recuperar usuário do banco de dados pelo email. Se o usuário não existir, insira no banco.
		User user = userRepository.findByEmail(dto.getEmail());
		if (user == null) {
			user = new User();
			user.setEmail(dto.getEmail());
			user = userRepository.saveAndFlush(user);
		}

		//Pegar filme
		Movie movie = movieRepository.findById(dto.getMovieId()).get();
		
		//Associar avaliação com Movie e User
		Score score = new Score();
		score.setUser(user);
		score.setMovie(movie);
		score.setValue(dto.getScore());

		//Salvar a avaliação do usuário para o dado filme.
		score = scoreRepository.saveAndFlush(score);
		
		//Somar as avaliações associadas ao filme
		double sum = 0.0;
		for(Score s : movie.getScores()) {
			sum = sum + s.getValue();
		}
		
		//Recalcular a avaliação média do filme
		double avg = sum / movie.getScores().size();
		
		//Associar média com filme
		movie.setScore(avg);
		
		//Associar somatória com filme
		movie.setCount(movie.getScores().size());
		
		//Salvar no DB
		movie = movieRepository.save(movie);	
		
		return new MovieDTO(movie);
	}

}
